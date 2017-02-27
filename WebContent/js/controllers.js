(function (global) {

  'use strict';
  /*global angular, console*/
  var webapp = angular.module('webapp', ['services', 'directives'])
    .controller('loginCtrl', ['$rootScope', '$scope', '$http', 'messageBus', 'servlets', function ($rootScope, $scope, $http, messageBus, servlets) {
      // various user data, that used in application forms, and chat
      $rootScope.user = {
        username: "",
        password: "",
        nickname: "",
        description: "",
        avatarUrl: ""
      };
      $scope.warning = "";
      $scope.warningAddition = "";
      // variables that controls hide and show of login form and auth error warning
      $scope.loginScreenHidden = false;
      $scope.authFailureWarningHidden = true;

      // called when user clicks "sign in" button on login screen
      $scope.login = function () {
        if (!$rootScope.user.username || !$rootScope.user.password ||
          $rootScope.user.username.length <= 0 || $rootScope.user.password <= 0) {
          $scope.authFailureWarningHidden = false;
          $scope.warning = "Username or password cannot be empty.";
          $scope.warningAddition = "Please try again";
          return;
        }
        $scope.authFailureWarningHidden = true;
        var credentials = {
          username: $rootScope.user.username,
          password: $rootScope.user.password
        };
        servlets.send("login", credentials);
      };

      // called when clicking on "register" button. then, login form is hidden and register form is shown 
      $scope.doregister = function () {
        $scope.loginScreenHidden = true;
        $scope.authFailureWarningHidden = true;
        messageBus.send('register');
      };

      // if was auth failure, show warning
      $scope.$on('authFailure', function (event, data) {
        if (data.error === "Username does not exist" || data.error === "Incorrect Password") {
          $scope.authFailureWarningHidden = false;
          $scope.warning = data.error;
          $scope.warningAddition = "Please try again or register";
          return;
        }
      });

      // on successful login, hide login form
      $scope.$on('authSuccess', function (event, data) {
        $scope.loginScreenHidden = true;
      });

    }]).controller('registerCtrl', ['$rootScope', '$scope', '$http', 'messageBus', 'servlets',
                                    function ($rootScope, $scope, $http, messageBus, servlets) {

        $scope.warning = ""; // warnings to show on error
        $scope.warningAddition = "";
        $scope.registerScreenHidden = true; // registration form, shown on "register" button click
        $scope.wrongRegisterWarningHidden = true; // warning is shown if user already exists
        // when clicking submit on registration form, data sent to the server
        $scope.register = function () {
          if (!$rootScope.user.username || !$rootScope.user.password ||
            $rootScope.user.username.length <= 0 || $rootScope.user.password <= 0) {
            $scope.wrongRegisterWarningHidden = false;
            $scope.warning = "Username or password cannot be empty.";
            $scope.warningAddition = "Please try again";
            return;
          }
          servlets.send("register", $rootScope.user);
        };
        // when user clicks on "register" button on login screen, show registration form
        $scope.$on('register', function (event, data) {
          $scope.registerScreenHidden = false;
        });
        // when auth success was sent from server, hide register form.
        $scope.$on('authSuccess', function (event, data) {
          $scope.registerScreenHidden = true;
        });
        // upon registration, if username already registered, show warning                              
        $scope.$on('authFailure', function (event, data) {
          if (data.error === "Username already exists" && !$scope.registerScreenHidden) {
            $scope.wrongRegisterWarningHidden = false;
            $scope.warning = data.error;
            $scope.warningAddition = "Please choose different username";
            return;
          }
        });

     }]).controller('chatRoomsCtrl', ['$rootScope', '$scope', '$http', '$window', 'messageBus',
                                      'socket', 'servlets',
                                      function ($rootScope, $scope, $http, $window, messageBus, socket, servlets) {

        $scope.chatRoomsScreenHidden = true; // the main uI view of application
        $scope.channelSelected = false; // needed to show right side users list, unsubscribe option and description
        $scope.showCreateChannelForm = false; // new channel creation form, shown when clicking on "create channel" button.

        $scope.currentChannel = {}; // channel that user currently viewing
        $scope.currentChannelThread = {}; // current channel's thread that user reading
        $scope.subscribedChannels = []; // list of subscribed channels, appears on the left
        $scope.publicChannels = []; // list of public channels, that were discovered. appears only on search.
        $scope.privateChannels = []; // list of private channels, appears on left side.
        $scope.query = ""; // when discovering new public channels, this variable's value sent to server
        // this is current reply to id value. if its -1, message will be standalone thread inside a bigger channel's thread.
        // if it's value set to specific value, when clicking on reply link in thread, then it will be shown in threaded manner
        $scope.repliedToId = -1;
        $scope.lastMessage = ""; // variable for sending messages when participating in discussion

        // helper function that finds channel in list.
        // usage: .object returns channel object, .index returns index in list
        var findChannel = function (channelName, channelsList) {
          if (!channelsList) {
            return null;
          }
          for (var key = 0; key < channelsList.length; key++) {
            if (channelsList.hasOwnProperty(key)) {
              if (channelsList[key].channelId !== channelName) {
                continue;
              }
            }
            return {
              object: channelsList[key],
              index: key
            };
          }
          return null;
        };

        // helper function that finding message in array by it's id
        var findMessageIndexById = function (id, thread) {
          for (var i = 0; i < thread.length; ++i) {
            if (id !== thread[i].message.id) {
              continue;
            }
            return i;
          }
          return -1;
        };

        // function to recursively insert reply into thread by id of message, 
        // that reply message replies to.
        var appendToThreadById = function (replyMessage, thread) {
          for (var i = 0; i < thread.length; ++i) {
            if (replyMessage.message.repliedToId === thread[i].message.id) {
              thread[i].replies.push(replyMessage);
              return;
            }
            if (thread[i].replies.length > 0) {
              appendToThreadById(replyMessage, thread[i].replies);
            }
          }
        };

        // this function called each time when needed to change current thread, 
        // for example, when changing a channel. it called also on successful subscription 
        // and on successful new channel creation. if current channel's thread empty,
        // it triggers downloadMessages json sending to server.
        var getCurrentThread = function (channelName, channelsList) {
          if (findChannel(channelName, channelsList).object.channelThread.length < 10) {
            downloadMessages(channelName);
          }
          return findChannel(channelName, channelsList).object.channelThread;
        };

        // This is helper function, that does common operations to four json requests                                
        var sendJson = function (channelName, messageType) {
          var messageJson = {
            messageType: messageType,
            messageContent: {
              channelId: channelName
            }
          };
          socket.send(messageJson);
        };

        // this function called each time on entering channel and current channel
        // thread is empty, or when inside channel and chat area scrolled down fully
        var downloadMessages = function (channelName) {
          sendJson(channelName, "downloadMessages");
        };

        // this is special message sent to server, when starting to view
        // one of the channels. this helps server to determine, when needed
        // to send messages on specific channel, or only updates of unread
        // messages counters.
        var viewingChannel = function (channelName) {
          sendJson(channelName, "channelViewing");
        };

        // new subscription to channel. called when trying to enter
        // to channel, that user not subscribed to it (on channel discovery).
        var subscribeToChannel = function (channelName) {
          sendJson(channelName, "subscribe");
        };

        // this function called when a button "unsubscribe" clicked. 
        $scope.unsubscribeChannel = function (channelName) {
          sendJson(channelName, "unsubscribe");
        };

        // new channel creation. called on click on the green button "create channel".
        // username parameter needed for private channel creation 
        // (this username is of other participiant, not that initiated chat.)
        $scope.createChannel = function (channelName, description, username) {
          if (!channelName) {
            return;
          }
          var createChannelJson = {
            messageType: "createChannel",
            messageContent: {
              channelId: channelName,
              description: description,
              username: username
            }
          };
          socket.send(createChannelJson);
        };

        // this function is for entering public channels by clicking on subscribed channels list.
        // if one of discovered channels clicked, then it called and make new subscription.
        $scope.enterChannel = function (channelName, channelsList) {
          if (!findChannel(channelName, channelsList) || (channelsList === $scope.publicChannels)) {
            /// the only possible case is when on channel discovery and clicking on public channel
            subscribeToChannel(channelName);
          } else {
            viewingChannel(channelName);
            $scope.channelSelected = true;
            $scope.repliedToId = -1;
            $scope.currentChannel = findChannel(channelName, channelsList).object;
            $scope.currentChannelThread = getCurrentThread(channelName, channelsList);
          }
        };

        // this function called when clicking on private channel in 
        // private channels list, or on nickname in users list, or on nickname
        // somewhere in thread of the chat
        $scope.enterPrivateChannel = function (dstUsername, dstNickname) {
          if (dstUsername === $scope.user.username) {
            return;
          }
          var possibleChannelName1 = $scope.user.username + dstUsername;
          var possibleChannelName2 = dstUsername + $scope.user.username;
          var finalName = null;
          if (findChannel(possibleChannelName1, $scope.privateChannels)) {
            finalName = possibleChannelName1;
          } else if (findChannel(possibleChannelName2, $scope.privateChannels)) {
            finalName = possibleChannelName2;
          }
          if (!finalName) {
            var description = "Private channel for " + $scope.user.nickname + " and " + dstNickname + ", created by " + $scope.user.nickname;
            $scope.createChannel(possibleChannelName1, description, dstUsername);
            return;
          }
          $scope.enterChannel(finalName, $scope.privateChannels);
        };

        // this is callback that called when thread is scrolled down
        // (with mouse wheel or page down, down arrow keys)
        $scope.downloadOnScroll = function () {
          downloadMessages($scope.currentChannel.channelId);
        };

        // channel discovery, called on hitting enter in "search channel.." 
        // field near user description.
        $scope.discoverChannels = function (query) {
          var queryJson = {
            messageType: "channelDiscovery",
            messageContent: {
              query: query,
            }
          };
          socket.send(queryJson);
        };

        // send message in current channel. in order to see sent messages, 
        // we download messages from server. this function called on hitting enter
        // in input text field inside chat, or on clicking a button near this field
        $scope.sendMessage = function (message) {
          var sendMessageJson = {
            messageType: "sendMessage",
            messageContent: {
              message: {
                channelId: $scope.currentChannel.channelId,
                repliedToId: $scope.repliedToId,
                content: message
              }
            }
          };
          socket.send(sendMessageJson);
          $scope.lastMessage = '';
        };

        // helper function to change appearance of active elements  
        $scope.isActive = function (channelName) {
          return $scope.currentChannel.channelId === channelName;
        };
        // this function called on click "reply" in thead.
        $scope.setReply = function (repliedToId) {
          $scope.repliedToId = repliedToId;
        };

        // original private channel name is of the form: username1+username2, but in interface we want to
        // show nickname of other user, because this is what user saw whan initiated private chat.
        $scope.getPrivateChannelName = function (channelName) {
          var privateChannelNameToShow = "";
          var channel = findChannel(channelName, $scope.privateChannels).object;
          for (var i = 0; i < channel.users.length; ++i) {
            if (channel.users[i].username.toLowerCase().indexOf($scope.user.username.toLowerCase()) != -1) {
              continue;
            }
            privateChannelNameToShow = channel.users[i].nickname;
          }
          return privateChannelNameToShow;
        };

        // this is helper binary function to additionally filter discovered channels, after hitting enter but before
        // hitting enter again. this way query can be changed and it possible to restict search results even more 
        // without bothering the server with requests.
        $scope.searchChannel = function (channel) {
          if (!$scope.query || (channel.channelId.toLowerCase().indexOf($scope.query.toLowerCase()) != -1)) {
            return true;
          }
          for (var i = 0; i < channel.users.length; ++i) {
            if (channel.users[i].nickname.toLowerCase().indexOf($scope.query.toLowerCase()) != -1) {
              return true;
            }
          }
          return false;
        };
        // function callback, that common to two events - channelSuccess 
        // and subscribeSuccess.                                
        var pushChannelAndEnter = function (event, response) {
          var channelsList;
          if (response.channel.isPublic === true) {
            $scope.subscribedChannels.push(response.channel);
            channelsList = $scope.subscribedChannels;
          } else {
            $scope.privateChannels.push(response.channel);
            channelsList = $scope.privateChannels;
          }
          $scope.$digest();
          $scope.channelSelected = true;
          $scope.currentChannelThread = getCurrentThread(response.channel.channelId, channelsList);
          $scope.currentChannel = findChannel(response.channel.channelId, channelsList).object;
        };
        // this event is happens on successful registration or login. then main interface is
        // shown to user, and lists of subscribed and private channels updated from response.
        $scope.$on('authSuccess', function (event, response) {
          $scope.chatRoomsScreenHidden = false;
          $rootScope.user = response.user;
          $scope.subscribedChannels = response.subscribedChannels;
          $scope.privateChannels = response.privateChannels;
          socket.connect($rootScope.user.username);
        });

        // this event happens on succesful subscription on channel. first case is
        // when user clicking on channel when discovering channels, second case
        // is when some user initiate private chat creation by clicking on nickname
        // in thread or in users list on the right side of main chat interface.
        $scope.$on('subscribeSuccess', pushChannelAndEnter);

        // event, that sent from server on channel creation. in this case
        // current thread and current channel are updated to be of this new channel,
        // (user enters the channel).
        $scope.$on('channelSuccess', pushChannelAndEnter);

        // this happens, when someone enters channel, so users list need to be updated  
        $scope.$on('userSubscribed', function (event, response) {
          var channel = findChannel(response.channelId, $scope.subscribedChannels);
          if (!channel) {
            channel = findChannel(response.channelId, $scope.privateChannels);
          }
          channel.object.users.push(response.user);
          channel.object.numberOfSubscribers = channel.object.numberOfSubscribers + 1;
          $scope.$digest();
        });

        // this happens, when someone exits channel, so users list need to be updated  
        $scope.$on('userUnsubscribed', function (event, response) {
          var channel = findChannel(response.channelId, $scope.subscribedChannels);
          if (!channel) {
            channel = findChannel(response.channelId, $scope.privateChannels);
          }
          channel.object.users.pop(response.username);
          channel.object.numberOfSubscribers = channel.object.numberOfSubscribers - 1;
          $scope.$digest();
        });

        // happens when user sent unsubscribe request (clicked on "unsubscribe" button),
        // and server sent successful reply
        $scope.$on('unsubscribe', function (event, response) {
          var channelsList = $scope.subscribedChannels;
          var channel = findChannel(response.channelId, channelsList);
          if (!channel) {
            channelsList = $scope.privateChannels;
          }
          channelsList.splice(findChannel(response.channelId, channelsList).index, 1);

          if ($scope.currentChannel.channelId === response.channelId) {
            $scope.currentChannel = {};
            $scope.currentChannelThread = {};
            $scope.channelSelected = false;
          }
          $scope.$digest();
        });

        // this event happens, if user scrolled down in current channel with mouse wheel,
        // with page down or arrow down keys (after click on chat area), or when sending message 
        // in chat. 
        $scope.$on('downloadMessages', function (event, response) {
          var channelsList = $scope.subscribedChannels;
          var channel = findChannel(response.channelId, channelsList);
          if (!channel) {
            channelsList = $scope.privateChannels;
            channel = findChannel(response.channelId, channelsList);
          }
          if (!response.channelThread || !response.channelThread.length || !channel) {
            return;
          }
          // iterate on channel thread, remove all root messages, and push arrived root messages
          // this way we move thread down and update lastModified property in whole thread
          for (var i = 0; i < response.channelThread.length; i++) {
            if (response.channelThread[i].message.repliedToId !== -1) {
              continue;
            }
            var index = findMessageIndexById(response.channelThread[i].message.id, channel.object.channelThread);
            if (index !== -1) {
              channel.object.channelThread.splice(index, 1);
            }
            channel.object.channelThread.push(response.channelThread[i]);
          }
          // append replies to root messages according to tree structure
          for (var j = 0; j < response.channelThread.length; j++) {
            appendToThreadById(response.channelThread[j], channel.object.channelThread);
          }
          channel.object.unreadMessages = response.unreadMessages;
          channel.object.unreadMentionedMessages = response.unreadMentionedMessages;
          $scope.$digest();
        });

        // this event happens, when server replies on channel search query.  
        $scope.$on('channelDiscovery', function (event, response) {
          angular.merge($scope.publicChannels, $scope.publicChannels, response.channels);
          $scope.$digest();
        });
        // update couters needed, when number of messages or mentioned messages changing in
        // a channel, that user not currently viewing.  
        $scope.$on('updateCounters', function (event, response) {
          var channel = findChannel(response.channelId, $scope.subscribedChannels);
          if (!channel) {
            channel = findChannel(response.channelId, $scope.privateChannels);
          }
          channel.object.unreadMessages = response.unreadMessages;
          channel.object.unreadMentionedMessages = response.unreadMentionedMessages;
          $scope.$digest();
        });

      }]);
}(this.window));