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

      $scope.subscribeToChannel = function (channelName) {
        // If channel already discovered, push to subscribed, if not, ask for subscription
        /*if (findChannel(channelName, $scope.publicChannels)) {
          $scope.subscribedChannels.push(findChannel(channelName, $scope.publicChannels).object);
          return;
        }*/
        var subscribeJson = {
          MessageType: "Subscribe",
          MessageContent: {
            ChannelId: channelName,
          }
        };
        //console.log('in subscribeToChannel(): Sending: ' + JSON.stringify(subscribeJson));
        Socket.send(subscribeJson);
      };

      $scope.enterChannel = function (channelName, channelsList) {
        $scope.channelSelected = true;
        if (!findChannel(channelName, channelsList) || (channelsList === $scope.publicChannels)) {
          /// The only possible case is when on channel discovery and clicking on public channel
          $scope.subscribeToChannel(channelName);
        } else {
          viewingChannel(channelName);
          $scope.currentChannelThread = getCurrentThread(channelName, channelsList);
          $scope.currentChannel = findChannel(channelName, channelsList).object;
        }
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

      $scope.enterPrivateChannel = function (dstUsername, dstNickname) {
        console.log('in enterPivateChannel(): entering: dstUsername, dstNickname: ' + JSON.stringify(dstUsername) + JSON.stringify(dstNickname));
        if (dstUsername === $scope.user.Username) {
          return;
        }
        $scope.channelSelected = true;
        var possibleChannelName1 = $scope.user.Username + dstUsername;
        var possibleChannelName2 = dstUsername + $scope.user.Username;
        var finalName = null;
        if (findChannel(possibleChannelName1, $scope.privateChannels)) {
          finalName = possibleChannelName1;
        } else if (findChannel(possibleChannelName2, $scope.privateChannels)) {
          finalName = possibleChannelName2;
        }
        if (!finalName) {
          //console.log('enterPrivateChannel: private channel not found!');
          var description = "Private channel for " + $scope.user.Nickname + " and " + dstNickname + ", created by " + $scope.user.Nickname;
          $scope.createChannel(possibleChannelName1, description, dstUsername);
          return;
        }
        //console.log('in enterPivateChannel(): entering: ' + JSON.stringify(finalName));
        $scope.viewingChannel(finalName);
        $scope.currentChannelThread = getCurrentThread(finalName, $scope.privateChannels);
        $scope.currentChannel = findChannel(finalName, $scope.privateChannels).object;
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

      $scope.downloadOnScroll = function () {
        if ($scope.channelSelected === true) {
          downloadMessages($scope.currentChannel.ChannelName);
        }
      };

      $scope.discoverChannels = function (query) {

        var queryJson = {
          MessageType: "ChannelDiscovery",
          MessageContent: {
            Query: query,
          }
        };
        //console.log('in discoverChannels(): Sending: ' + JSON.stringify(queryJson));
        Socket.send(queryJson);
      };

      $scope.unsubscribeChannel = function (channelName) {
        //console.log('in unsubscribeChannel()');
        var unsubscribeJson = {
          MessageType: "Unsubscribe",
          MessageContent: {
            ChannelId: channelName
          }
        };
        Socket.send(unsubscribeJson);
      };

      $scope.sendMessage = function (message) {
        //console.log('in sendMessage()');
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

      $scope.viewingChannel = function (channelName) {
        var viewingChannelJson = {
          MessageType: "ChannelViewing",
          MessageContent: {
            Message: {
              ChannelId: channelName
            }
          }
        };
        Socket.send(viewingChannelJson);
      };

      $scope.isActive = function (channelName) {
        return $scope.currentChannel.ChannelName === channelName;
      };

      $scope.setReply = function (repliedToId) {
        console.log('ChatRoomsCtrl: setReply(): repliedToId is:', repliedToId);
        $scope.repliedToId = ($scope.repliedToId === repliedToId) ? -1 : repliedToId;
      };

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

      $scope.$on('AuthSuccess', function (event, response) {
        //console.log('ChatRoomsCtrl: got event AuthSuccess');
        $scope.chatRoomsScreenHidden = false;
        $rootScope.user = response.User;
        //console.log('in ChatRoomsCtrl:(): public channels:' + JSON.stringify(response.PublicChannels));
        //console.log('in ChatRoomsCtrl:(): subscribed channels:' + JSON.stringify(response.SubscribedChannels));
        $scope.subscribedChannels = response.SubscribedChannels;
        $scope.privateChannels = response.PrivateChannels;

        Socket.connect($rootScope.user.Username);
      });

      $scope.$on('SubscribeSuccess', function (event, response) {
        //console.log('ChatRoomsCtrl: got event SubscribeSuccess');
        var channelsList;
        if (response.Channel.IsPublic === true) {
          $scope.subscribedChannels.push(response.Channel);
          channelsList = $scope.subscribedChannels;
        } else {
          $scope.privateChannels.push(response.Channel);
          channelsList = $scope.privateChannels;
        }

        $scope.$digest();

        //console.log('ChatRoomsCtrl: after channelsList.push(response.Channel):' + JSON.stringify(list));
        $scope.channelSelected = true;
        $scope.currentChannelThread = getCurrentThread(response.Channel.ChannelName, channelsList);
        $scope.currentChannel = findChannel(response.Channel.ChannelName, channelsList).object;
      });

      $scope.$on('ChannelSuccess', function (event, response) {
        //console.log('ChatRoomsCtrl: got event ChannelSuccess');
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

      $scope.$on('UserSubscribed', function (event, response) {
        //console.log('ChatRoomsCtrl: got event UserSubscribed');
        var channel = findChannel(response.Channel, $scope.subscribedChannels);
        if (!channel) {
          channel = findChannel(response.Channel, $scope.privateChannels);
        }
        channel.Users.push(response.User.Username);
      });

      $scope.$on('UserUnsubscribed', function (event, response) {
        //console.log('ChatRoomsCtrl: got event UserUnsubscribed');
        var channel = findChannel(response.Channel, $scope.subscribedChannels);
        if (!channel) {
          channel = findChannel(response.Channel, $scope.privateChannels);
        }
        channel.Users.pop(response.Username);
      });

      $scope.$on('Unsubscribe', function (event, response) {
        //console.log('ChatRoomsCtrl: got event Unsubscribe');
        //console.log('Deleting: ' + findChannel(channelname, $scope.subscribedChannels).index);
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

      $scope.$on('DownloadMessages', function (event, response) {
        //console.log('ChatRoomsCtrl: got event DownloadMessages');
        var channelsList = $scope.subscribedChannels;
        var channel = findChannel(response.Channel, channelsList);
        if (!channel) {
          channelsList = $scope.privateChannels;
          channel = findChannel(response.Channel, channelsList);
        }
        //console.log('ChatRoomsCtrl: got event DownloadMessages for channel ' + response.Channel);

        //console.log('ChatRoomsCtrl: got event DownloadMessages for channel ' + channel.object.ChannelName);
        if (!response.ChannelThread || !response.ChannelThread.length || !channel) {
          //console.log('ChatRoomsCtrl: got event DownloadMessages: !response.ChannelThread || !response.ChannelThread.length || !channel happened  ');
          return;
        }
        for (var i = 0; i < response.ChannelThread.length; i++) {
          if (response.ChannelThread[i].Message.RepliedToId !== -1) {
            continue;
          }
          //console.log('DownloadMessages: channel.object.ChannelThreadd before push: response.ChannelThread[i]:', JSON.stringify(channel.object.ChannelThread));
          var index = findMessageIndexById(i, channel.object.ChannelThread);
          if (index !== -1) {
            channel.object.ChannelThread.splice(i, 1);
          }
          channel.object.ChannelThread.push(response.ChannelThread[i]);
          ///console.log('DownloadMessages: channel.object.ChannelThreadd after push: response.ChannelThread[i]:', JSON.stringify(channel.object.ChannelThread));
          //console.log('DownloadMessages: removing from response.ChannelThread after push: response.ChannelThread[i]:', JSON.stringify(response.ChannelThread[i]));
          //response.ChannelThread.splice(i, 1);
        }
        for (var j = 0; j < response.ChannelThread.length; j++) {
          appendToThreadById(response.ChannelThread[j], channel.object.ChannelThread);
          //response.ChannelThread.splice(j, 1);
        }

        channel.object.unreadMessages = response.unreadMessages;
        channel.object.unreadMentionedMessages = response.unreadMentionedMessages;

        //        channel.object.ChannelThread = channel.object.ChannelThread.concat(response.ChannelThread);
        //        angular.merge(channel.object.ChannelThread, channel.object.ChannelThread, response.ChannelThread);

        //findChannel(response.Channel, channelsList).object.ChannelThread = channel.object.ChannelThread;
        //$scope.currentChannel = findChannel(response.Channel, channelsList).object;
        //$scope.currentChannelThread = getCurrentThread(response.Channel, channelsList);
        $scope.$digest();
      });

      $scope.$on('ChannelDiscovery', function (event, response) {
        //console.log('ChatRoomsCtrl: got event ChannelDiscovery');
        angular.merge($scope.publicChannels, $scope.publicChannels, response.Channels);
        $scope.$digest();
      });

      $scope.$on('UpdateCounters', function (event, response) {
        //console.log('ChatRoomsCtrl: got event UpdateCounters');
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