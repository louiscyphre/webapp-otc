(function (global) {

  'use strict';
  /*global angular, console*/
  var WebChat = angular.module('webChat', ['constants', 'services', 'directives'])
    .controller('LoginCtrl', ['$rootScope', '$scope', '$http', 'MessageBus', 'Servlets', function ($rootScope, $scope, $http, MessageBus, Servlets) {

      $rootScope.user = {
        Username: "",
        Password: "",
        Nickname: "",
        Description: "",
        AvatarUrl: ""
      };

      $scope.loginScreenHidden = false;
      $scope.authFailureWarningHidden = true;

      $scope.login = function () {

        var credentials = {
          Username: $scope.user.Username,
          Password: $scope.user.Password
        };
        //console.log('in login(): Sending: ' + JSON.stringify(credentials));
        Servlets.send("login", credentials);
      };

      $scope.doregister = function () {
        //console.log('in doregister(): Sending event register');
        $scope.loginScreenHidden = true;
        $scope.authFailureWarningHidden = true;
        MessageBus.send('register');
      };

      $scope.$on('AuthFailure', function (event, data) {
        //console.log('AuthFailure!!!!!: ');
        if (data.Error === "Username does not exist" || data.Error === "Incorrect Password") {
          $scope.authFailureWarningHidden = false;
          return;
        }
      });

      $scope.$on('AuthSuccess', function (event, data) {
        $scope.loginScreenHidden = true;
      });

    }]).controller('RegisterCtrl', ['$rootScope', '$scope', '$http', 'MessageBus', 'Servlets', function ($rootScope, $scope, $http, MessageBus, Servlets) {

      $scope.registerScreenHidden = true;
      $scope.userExistsWarningHidden = true;

      $scope.register = function () {
        //console.log('in register(): Sending: ' + JSON.stringify($rootScope.user));
        Servlets.send("register", $rootScope.user);
      };

      $scope.$on('register', function (event, data) {
        //console.log('RegisterCtrl: got event register');
        $scope.registerScreenHidden = false;
      });
      $scope.$on('AuthSuccess', function (event, data) {
        //console.log('RegisterCtrl: got event AuthSuccess');
        $scope.registerScreenHidden = true;
      });

     }]).controller('ChatRoomsCtrl', ['$rootScope', '$scope', '$http', '$window', 'MessageBus', 'Socket', 'Servlets', function ($rootScope, $scope, $http, $window, MessageBus, Socket, Servlets) {

      $scope.chatRoomsScreenHidden = true;
      $scope.createChannelFormHidden = true;
      $scope.channelSelected = false;
      $scope.showCreateChannelForm = false;

      $scope.currentChannel = {};
      $scope.currentChannelThread = {};
      $scope.subscribedChannels = [];
      $scope.publicChannels = [];
      $scope.privateChannels = [];
      $scope.query = "";
      $scope.repliedToId = -1;
      $scope.lastMessage = "";

      var findChannel = function (channelName, channelsList) {
        //console.log('findChannelByName: entering ' + channelName);
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

      var findMessageIndexById = function (id, thread) {
        for (var i = 0; i < thread.length; ++i) {
          if (id !== thread[i].Message.Id) {
            continue;
          }
          return i;
        }
        return -1;
      };

      var appendToThreadById = function (replyMessage, thread) {
        //console.log('in appendToThreadById(): appending a: ' + JSON.stringify(replyMessage));
        //console.log('in appendToThreadById(): appending to: ' + JSON.stringify(thread));
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

      var getCurrentThread = function (channelName, channelsList) {
        //console.log('getCurrentThread: entering ');
        if (!findChannel(channelName, channelsList).object.ChannelThread.length) {
          downloadMessages(channelName);
        }
        return findChannel(channelName, channelsList).object.ChannelThread;
      };

      var downloadMessages = function (channelName) {
        var downloadMessagesJson = {
          MessageType: "DownloadMessages",
          MessageContent: {
            Channel: channelName
          }
        };
        //console.log('in downloadMessages(): Sending: ' + JSON.stringify(downloadMessagesJson));
        if ($scope.channelSelected === true) {
          //console.log('Channel selected: ' + channelName);
          Socket.send(downloadMessagesJson);
        }
      };

      var viewingChannel = function (channelName) {
        var viewingChannelJson = {
          MessageType: "ChannelViewing",
          MessageContent: {
            channel: channelName
          }
        };
        Socket.send(viewingChannelJson);
      };

      $scope.subscribeToChannel = function (channelName) {
        var subscribeJson = {
          MessageType: "Subscribe",
          MessageContent: {
            ChannelId: channelName,
          }
        };
        //console.log('in subscribeToChannel(): Sending: ' + JSON.stringify(subscribeJson));
        Socket.send(subscribeJson);
      };

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
        //console.log('in discoverChannels(): Sending: ' + JSON.stringify(createChannelJson));
        Socket.send(createChannelJson);
      };

      // This function is for entering public channels by clicking
      /*$scope.enterChannel = function (channelName, channelsList) {
        var channel = findChannel(channelName, channelsList);
        if (channel || channelsList !== $scope.publicChannels) {
          viewingChannel(channelName);
          $scope.channelSelected = true;
          $scope.repliedToId = -1;
          $scope.currentChannelThread = getCurrentThread(channelName, channelsList);
          $scope.currentChannel = findChannel(channelName, channelsList).object;
        } else {
          /// The only possible case is when on channel discovery and clicking on public channel
          $scope.subscribeToChannel(channelName);
        }
      };*/
      // This function is for entering public channels by clicking
      $scope.enterChannel = function (channelName, channelsList) {
        if (!findChannel(channelName, channelsList) || (channelsList === $scope.publicChannels)) {
          /// The only possible case is when on channel discovery and clicking on public channel
          $scope.subscribeToChannel(channelName);
        } else {
          viewingChannel(channelName);
          $scope.channelSelected = true;
          $scope.repliedToId = -1;
          $scope.currentChannelThread = getCurrentThread(channelName, channelsList);
          $scope.currentChannel = findChannel(channelName, channelsList).object;
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
        viewingChannel(finalName);
        $scope.channelSelected = true;
        $scope.repliedToId = -1;
        $scope.currentChannelThread = getCurrentThread(finalName, $scope.privateChannels);
        $scope.currentChannel = findChannel(finalName, $scope.privateChannels).object;
      };

      // This is callback that called when thread is scrolled down
      // (with mouse wheel or page down, down arrow keys)
      $scope.downloadOnScroll = function () {
        if ($scope.channelSelected === true) {
          downloadMessages($scope.currentChannel.ChannelName);
        }
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
        downloadMessages($scope.currentChannel.ChannelName);
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
      // in thread or in users list on the right side of main chat inteface.
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