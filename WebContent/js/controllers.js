(function (global) {

  'use strict';
  /*global angular, console*/
  var WebChat = angular.module('webChat', ['services', 'directives'])
    .controller('LoginCtrl', ['$rootScope', '$scope', '$http', 'MessageBus', 'Servlets', function ($rootScope, $scope, $http, MessageBus, Servlets) {
      // Various user data, tha used in application forms, and chat
      $rootScope.user = {
        Username: "",
        Password: "",
        Nickname: "",
        Description: "",
        AvatarUrl: ""
      };
      // Variables that controls hide and show of login form and auth error warning
      $scope.loginScreenHidden = false;
      $scope.authFailureWarningHidden = true;

      // Called when user clicks "Sign in" button on login screen
      $scope.login = function () {
        var credentials = {
          Username: $scope.user.Username,
          Password: $scope.user.Password
        };
        Servlets.send("login", credentials);
      };

      // Called when clicking on "Register" button. Then, login form is hidden and register form is shown 
      $scope.doregister = function () {
        $scope.loginScreenHidden = true;
        $scope.authFailureWarningHidden = true;
        MessageBus.send('register');
      };

      // If was auth failure, show warning
      $scope.$on('AuthFailure', function (event, data) {
        if (data.Error === "Username does not exist" || data.Error === "Incorrect Password") {
          $scope.authFailureWarningHidden = false;
          return;
        }
      });

      // On successful login, hide login form
      $scope.$on('AuthSuccess', function (event, data) {
        $scope.loginScreenHidden = true;
      });

    }]).controller('RegisterCtrl', ['$rootScope', '$scope', '$http', 'MessageBus', 'Servlets',
                                    function ($rootScope, $scope, $http, MessageBus, Servlets) {

        $scope.registerScreenHidden = true; // Registration form, shown on "Register" button click
        $scope.userExistsWarningHidden = true; // Warning is shown if user already exists
        // When clicking submit on registration form, data sent to the server
        $scope.register = function () {
          Servlets.send("register", $rootScope.user);
        };
        // When user clicks on "Register" button on login screen, show registration form
        $scope.$on('register', function (event, data) {
          $scope.registerScreenHidden = false;
        });
        // When auth success was sent from server, hide register form.
        $scope.$on('AuthSuccess', function (event, data) {
          $scope.registerScreenHidden = true;
        });
        // Upon registration, if username already registered, show warning                              
        $scope.$on('AuthFailure', function (event, data) {
          if (data.Error === "Username already exists" && !$scope.registerScreenHidden) {
            $scope.userExistsWarningHidden = false;
            return;
          }
        });

     }]).controller('ChatRoomsCtrl', ['$rootScope', '$scope', '$http', '$window', 'MessageBus',
                                      'Socket', 'Servlets',
                                      function ($rootScope, $scope, $http, $window, MessageBus, Socket, Servlets) {

        $scope.chatRoomsScreenHidden = true; // The main UI view of application
        $scope.channelSelected = false; // Needed to show right side users list, unsubscribe option and description
        $scope.showCreateChannelForm = false; // New channel creation form, shown when clicking on "Create channel" button.

        $scope.currentChannel = {}; // Channel that user currently viewing
        $scope.currentChannelThread = {}; // Current channel's thread that user reading
        $scope.subscribedChannels = []; // List of subscribed channels, appears on the left
        $scope.publicChannels = []; // List of public channels, that were discovered. Appears only on search.
        $scope.privateChannels = []; // List of private channels, appears on left side.
        $scope.query = ""; // When discovering new public channels, this variable's value sent to server
        // This is current reply to id value. If its -1, message will be standalone thread inside a bigger channel's thread.
        // If it's value set to specific value, when clicking on reply link in thread, then it will be shown in threaded manner
        $scope.repliedToId = -1;
        $scope.lastMessage = ""; // Variable for sending messages when participating in discussion

        // Helper function that finds channel in list.
        // Usage: .object returns channel object, .index returns index in list
        var findChannel = function (channelName, channelsList) {
          if (!channelsList) {
            return null;
          }
          for (var key = 0; key < channelsList.length; key++) {
            if (channelsList.hasOwnProperty(key)) {
              if (channelsList[key].ChannelName !== channelName) {
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

        // Helper function that finding message in array by it's Id
        var findMessageIndexById = function (id, thread) {
          for (var i = 0; i < thread.length; ++i) {
            if (id !== thread[i].Message.Id) {
              continue;
            }
            return i;
          }
          return -1;
        };

        // Function to recursively insert reply into thread by Id of message, 
        // that reply message replies to.
        var appendToThreadById = function (replyMessage, thread) {
          for (var i = 0; i < thread.length; ++i) {
            if (replyMessage.Message.RepliedToId === thread[i].Message.Id) {
              thread[i].Replies.push(replyMessage);
              return;
            }
            if (thread[i].Replies.length > 0) {
              appendToThreadById(replyMessage, thread[i].Replies);
            }
          }
        };

        // This function called each time when needed to change current thread, 
        // for example, when changing a channel. It called also on successful subscription 
        // and on successful new channel creation. If current channel's thread empty,
        // it triggers downloadMessages json sending to server.
        var getCurrentThread = function (channelName, channelsList) {
          if (!findChannel(channelName, channelsList).object.ChannelThread.length) {
            downloadMessages(channelName);
          }
          return findChannel(channelName, channelsList).object.ChannelThread;
        };

        // This function called each time on entering channel and current channel
        // thread is empty, or when inside channel and chat area scrolled down fully
        var downloadMessages = function (channelName) {
          var downloadMessagesJson = {
            MessageType: "DownloadMessages",
            MessageContent: {
              Channel: channelName
            }
          };
          //if ($scope.channelSelected === true) {
          Socket.send(downloadMessagesJson);
          //}
        };

        // This is special message sent to server, when starting to view
        // one of the channels. This helps server to determine, when needed
        // to send messages on specific channel, or only updates of unread
        // messages counters.
        var viewingChannel = function (channelName) {
          var viewingChannelJson = {
            MessageType: "ChannelViewing",
            MessageContent: {
              channel: channelName
            }
          };
          Socket.send(viewingChannelJson);
        };

        // New subscription to channel. Called when trying to enter
        // to channel, that user not subscribed to it (on channel discovery).
        var subscribeToChannel = function (channelName) {
          var subscribeJson = {
            MessageType: "Subscribe",
            MessageContent: {
              ChannelId: channelName,
            }
          };
          Socket.send(subscribeJson);
        };

        // New channel creation. Called on click on the green button "Create channel".
        // username parameter needed for private channel creation 
        // (this username is of other participiant, not that initiated chat.)
        $scope.createChannel = function (channelName, description, username) {
          if (!channelName) {
            return;
          }
          var createChannelJson = {
            MessageType: "CreateChannel",
            MessageContent: {
              Name: channelName,
              Description: description,
              Username: username
            }
          };
          Socket.send(createChannelJson);
        };

        // This function is for entering public channels by clicking on subscribed channels list.
        // If one of discovered channels clicked, then it called and make new subscription.
        $scope.enterChannel = function (channelName, channelsList) {
          if (!findChannel(channelName, channelsList) || (channelsList === $scope.publicChannels)) {
            /// The only possible case is when on channel discovery and clicking on public channel
            subscribeToChannel(channelName);
          } else {
            viewingChannel(channelName);
            $scope.channelSelected = true;
            $scope.repliedToId = -1;
            $scope.currentChannel = findChannel(channelName, channelsList).object;
            $scope.currentChannelThread = getCurrentThread(channelName, channelsList);
          }
        };

        // This function called when clicking on private channel in 
        // private channels list, or on nickname in users list, or on nickname
        // somewhere in thread of the chat
        $scope.enterPrivateChannel = function (dstUsername, dstNickname) {
          if (dstUsername === $scope.user.Username) {
            return;
          }
          var possibleChannelName1 = $scope.user.Username + dstUsername;
          var possibleChannelName2 = dstUsername + $scope.user.Username;
          var finalName = null;
          if (findChannel(possibleChannelName1, $scope.privateChannels)) {
            finalName = possibleChannelName1;
          } else if (findChannel(possibleChannelName2, $scope.privateChannels)) {
            finalName = possibleChannelName2;
          }
          if (!finalName) {
            var description = "Private channel for " + $scope.user.Nickname + " and " + dstNickname + ", created by " + $scope.user.Nickname;
            $scope.createChannel(possibleChannelName1, description, dstUsername);
            return;
          }
          $scope.enterChannel(finalName, $scope.privateChannels);
        };

        // This is callback that called when thread is scrolled down
        // (with mouse wheel or page down, down arrow keys)
        $scope.downloadOnScroll = function () {
          //if ($scope.channelSelected === true) {
          downloadMessages($scope.currentChannel.ChannelName);
          //}
        };

        // Channel discovery, called on hitting enter in "Search channel.." 
        // field near user description.
        $scope.discoverChannels = function (query) {
          var queryJson = {
            MessageType: "ChannelDiscovery",
            MessageContent: {
              Query: query,
            }
          };
          Socket.send(queryJson);
        };

        // This function called when a button "Unsubscribed" clicked. 
        $scope.unsubscribeChannel = function (channelName) {
          var unsubscribeJson = {
            MessageType: "Unsubscribe",
            MessageContent: {
              ChannelId: channelName
            }
          };
          Socket.send(unsubscribeJson);
        };

        // Send message in current channel. In order to see sent messages, 
        // we download messages from server. This function called on hitting enter
        // in input text field inside chat, or on clicking a button near this field
        $scope.sendMessage = function (message) {
          var sendMessageJson = {
            MessageType: "SendMessage",
            MessageContent: {
              Message: {
                ChannelId: $scope.currentChannel.ChannelName,
                RepliedToId: $scope.repliedToId,
                Content: message
              }
            }
          };
          Socket.send(sendMessageJson);
          $scope.lastMessage = '';
        };

        // Helper function to change appearance of active elements  
        $scope.isActive = function (channelName) {
          return $scope.currentChannel.ChannelName === channelName;
        };
        // This function called on click "Reply" in thead.
        $scope.setReply = function (repliedToId) {
          $scope.repliedToId = repliedToId;
          console.log('ChatRoomsCtrl: setReply(): repliedToId is:', $scope.repliedToId);
        };

        // Original private channel name is of the form: username1+username2, but in interface we want to
        // show nickname of other user, because this is what user saw whan initiated private chat.
        $scope.getPrivateChannelName = function (channelName) {
          var privateChannelNameToShow = "";
          var channel = findChannel(channelName, $scope.privateChannels).object;
          for (var i = 0; i < channel.Users.length; ++i) {
            if (channel.Users[i].Username.toLowerCase().indexOf($scope.user.Username.toLowerCase()) != -1) {
              continue;
            }
            privateChannelNameToShow = channel.Users[i].Nickname;
          }
          return privateChannelNameToShow;
        };

        // This is helper binary function to additionally filter discovered channels, after hitting enter but before
        // hitting enter again. This way query can be changed and it possible to restict search results even more 
        // without bothering the server with requests.
        $scope.searchChannel = function (channel) {
          if (!$scope.query || (channel.ChannelName.toLowerCase().indexOf($scope.query.toLowerCase()) != -1)) {
            return true;
          }
          for (var i = 0; i < channel.Users.length; ++i) {
            if (channel.Users[i].Nickname.toLowerCase().indexOf($scope.query.toLowerCase()) != -1) {
              return true;
            }
          }
          return false;
        };

        // This event is happens on successful registration or login. Then main interface is
        // shown to user, and lists of subscribed and private channels updated from response.
        $scope.$on('AuthSuccess', function (event, response) {
          $scope.chatRoomsScreenHidden = false;
          $rootScope.user = response.User;
          $scope.subscribedChannels = response.SubscribedChannels;
          $scope.privateChannels = response.PrivateChannels;
          Socket.connect($rootScope.user.Username);
        });

        // This event happens on succesful subscription on channel. First case is
        // when user clicking on channel when discovering channels, second case
        // is when some user initiate private chat creation by clicking on nickname
        // in thread or in users list on the right side of main chat interface.
        $scope.$on('SubscribeSuccess', function (event, response) {
          var channelsList;
          if (response.Channel.IsPublic === true) {
            $scope.subscribedChannels.push(response.Channel);
            channelsList = $scope.subscribedChannels;
          } else {
            $scope.privateChannels.push(response.Channel);
            channelsList = $scope.privateChannels;
          }
          $scope.$digest();
          $scope.channelSelected = true;
          $scope.currentChannelThread = getCurrentThread(response.Channel.ChannelName, channelsList);
          $scope.currentChannel = findChannel(response.Channel.ChannelName, channelsList).object;
        });

        // Event, that sent from server on channel creation. In this case
        // current thread and current channel are updated to be of this new channel,
        // (user enters the channel).
        $scope.$on('ChannelSuccess', function (event, response) {
          var channelsList;
          if (response.Channel.IsPublic === true) {
            $scope.subscribedChannels.push(response.Channel);
            $scope.$digest();
            channelsList = $scope.subscribedChannels;
          } else {
            $scope.privateChannels.push(response.Channel);
            $scope.$digest();
            channelsList = $scope.privateChannels;
          }
          $scope.channelSelected = true;
          $scope.currentChannelThread = getCurrentThread(response.Channel.ChannelName, channelsList);
          $scope.currentChannel = findChannel(response.Channel.ChannelName, channelsList).object;
        });

        // This happens, when someone enters channel, so users list need to be updated  
        $scope.$on('UserSubscribed', function (event, response) {
          var channel = findChannel(response.Channel, $scope.subscribedChannels);
          if (!channel) {
            channel = findChannel(response.Channel, $scope.privateChannels);
          }
          channel.Users.push(response.User.Username);
        });

        // This happens, when someone exits channel, so users list need to be updated  
        $scope.$on('UserUnsubscribed', function (event, response) {
          var channel = findChannel(response.Channel, $scope.subscribedChannels);
          if (!channel) {
            channel = findChannel(response.Channel, $scope.privateChannels);
          }
          channel.Users.pop(response.Username);
        });

        // Happens when user sent unsubscribe request (clicked on "Unsubscribe" button),
        // and server sent successful reply
        $scope.$on('Unsubscribe', function (event, response) {
          var channelsList = $scope.subscribedChannels;
          var channel = findChannel(response.Channel, channelsList);
          if (!channel) {
            channelsList = $scope.privateChannels;
          }
          channelsList.splice(findChannel(response.Channel, channelsList).index, 1);

          if ($scope.currentChannel === response.Channel) {
            $scope.currentChannel = {};
            $scope.currentChannelThread = {};
            $scope.channelSelected = false;
          }
        });

        // This event happens, if user scrolled down in current channel with mouse wheel,
        // with page down or arrow down keys (after click on chat area), or when sending message 
        // in chat. 
        $scope.$on('DownloadMessages', function (event, response) {
          var channelsList = $scope.subscribedChannels;
          var channel = findChannel(response.Channel, channelsList);
          if (!channel) {
            channelsList = $scope.privateChannels;
            channel = findChannel(response.Channel, channelsList);
          }
          if (!response.ChannelThread || !response.ChannelThread.length || !channel) {
            return;
          }
          // Iterate on channel thread, remove all root messages, and push arrived root messages
          // This way we move thread down and update lastModified property in whole thread
          for (var i = 0; i < response.ChannelThread.length; i++) {
            if (response.ChannelThread[i].Message.RepliedToId !== -1) {
              continue;
            }
            var index = findMessageIndexById(response.ChannelThread[i].Message.Id, channel.object.ChannelThread);
            if (index !== -1) {
              channel.object.ChannelThread.splice(index, 1);
            }
            channel.object.ChannelThread.push(response.ChannelThread[i]);
          }
          // Append replies to root messages according to tree structure
          for (var j = 0; j < response.ChannelThread.length; j++) {
            appendToThreadById(response.ChannelThread[j], channel.object.ChannelThread);
          }
          channel.object.unreadMessages = response.unreadMessages;
          channel.object.unreadMentionedMessages = response.unreadMentionedMessages;
          $scope.$digest();
        });

        // This event happens, when server replies on channel search query.  
        $scope.$on('ChannelDiscovery', function (event, response) {
          angular.merge($scope.publicChannels, $scope.publicChannels, response.Channels);
          $scope.$digest();
        });
        // Update couters needed, when number of messages or mentioned messages changing in
        // a channel, that user not currently viewing.  
        $scope.$on('UpdateCounters', function (event, response) {
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