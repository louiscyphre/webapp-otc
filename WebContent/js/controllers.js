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
        console.log('in login(): Sending: ' + JSON.stringify(credentials));
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

      var appendToThreadById = function (reply, thread) {
        console.log('in appendToThreadById(): appending: ' + JSON.stringify(reply));
        for (var obj in thread) {
          if (reply.Message.RepliedToId === obj.Message.Id) {
            obj.Replies.push(reply);
            return;
          }
          if (obj.Replies.length > 0) {
            appendToThreadById(reply, obj.Replies);
          }
        }
      };

      var getCurrentThread = function (channelName, channelsList) {
        //console.log('getCurrentThread: entering ');
        if (!findChannel(channelName, channelsList).object.ChannelThread.length) {
          $scope.downloadMessages(channelName);
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

        //console.log('in enterChannel(): channelName: ' + JSON.stringify(channelname));
        //console.log('in enterChannel(): $scope.currentChannel: ' + $scope.currentChannel);    
        if (!findChannel(channelName, channelsList) || (channelsList === $scope.publicChannels)) {
          /// The only possible case is when on channel discovery and clicking on public channel
          //console.log('enterChannel: no subscribed channels!');
          $scope.subscribeToChannel(channelName);

        } else {
          $scope.currentChannelThread = getCurrentThread(channelName, channelsList);
          //console.log('ChatRoomsCtrl: got event DownloadMessages:  $scope.currentChannelThread: ' + JSON.stringify($scope.currentChannelThread));

          $scope.currentChannel = findChannel(channelName, channelsList).object;
        }
      };

      $scope.createChannel = function (channelName, description, username) {

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

      $scope.enterPrivateChannel = function (targetUsername, targetNickname) {
        console.log('in enterPivateChannel(): $scope.currentChannel: ' + $scope.currentChannel);
        if (targetUsername === $scope.user.Username) {
          return;
        }
        var possibleChannelName1 = $scope.user.Username + targetUsername;
        var possibleChannelName2 = targetUsername + $scope.user.Username;
        var finalName = null;
        if (findChannel(possibleChannelName1, $scope.privateChannels)) {
          finalName = possibleChannelName1;
        } else if (findChannel(possibleChannelName2, $scope.privateChannels)) {
          finalName = possibleChannelName2;
        }
        if (!finalName) {
          console.log('enterPrivateChannel: private channel not found!');
          var description = "Private channel for " + $scope.user.Nickname + " and " + targetNickname + ", created by " + $scope.user.Nickname;
          $scope.createChannel(possibleChannelName1, description, targetUsername);
          return;
        }
        console.log('in enterPivateChannel(): entering: ' + JSON.stringify(finalName));
        $scope.currentChannelThread = getCurrentThread(finalName, $scope.privateChannels);
        $scope.currentChannel = findChannel(finalName, $scope.privateChannels).object;
      };

      $scope.downloadMessages = function (channelName) {

        var downloadMessagesJson = {
          MessageType: "DownloadMessages",
          MessageContent: {
            Channel: channelName,
          }
        };
        //console.log('in downloadMessages(): Sending: ' + JSON.stringify(downloadMessagesJson));
        Socket.send(downloadMessagesJson);
      };

      $scope.downloadOnScroll = function () {
        $scope.downloadMessages($scope.currentChannel.Channel);

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
      };

      $scope.viewingChannel = function (channelName) {
        //console.log('in sendMessage()');
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

      $scope.reply = function (messageId) {
        $scope.repliedToId = ($scope.repliedToId === messageId) ? -1 : messageId;
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
        //get all channels and subscribed channels from json (must be here or on auth with servlet??)
        $rootScope.user = response.User;
        //console.log('in ChatRoomsCtrl:(): public channels:' + JSON.stringify(response.PublicChannels));
        //console.log('in ChatRoomsCtrl:(): subscribed channels:' + JSON.stringify(response.SubscribedChannels));
        $scope.subscribedChannels = response.SubscribedChannels;
        $scope.privateChannels = response.PrivateChannels;

        Socket.connect($rootScope.user.Username);
      });

      $scope.$on('SubscribeSuccess', function (event, response) {
        console.log('ChatRoomsCtrl: got event SubscribeSuccess');
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
        $scope.currentChannelThread = getCurrentThread(response.Channel, channelsList);
        $scope.currentChannel = findChannel(response.Channel, channelsList).object;
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
        /*if (response.ChannelThread[0].Message.RepliedToId !== -1) {
          appendToThreadById(response.ChannelThread[0], thread);
          response.ChannelThread.splice(0, 1);
        }*/

        //console.log('ChatRoomsCtrl: got event DownloadMessages: channel.object.ChannelThread.length ' + channel.object.ChannelThread.length);
        //console.log('ChatRoomsCtrl: got event DownloadMessages: response.ChannelThread.length ' + response.ChannelThread.length);
        angular.merge(channel.object.ChannelThread, channel.object.ChannelThread, response.ChannelThread);
        //findChannel(response.Channel, channelsList).object.ChannelThread = channel.object.ChannelThread;
        //console.log('ChatRoomsCtrl: got event DownloadMessages: channel.object.ChannelThread.length ' + channel.object.ChannelThread.length);
        //console.log('ChatRoomsCtrl: got event DownloadMessages: channel.object.ChannelThread: ' + JSON.stringify(findChannel(response.Channel, channelsList).object.ChannelThread));
        //$scope.currentChannel = findChannel(response.Channel, channelsList).object;
        //$scope.currentChannelThread = getCurrentThread(response.Channel, channelsList);
        $scope.$digest();
      });

      $scope.$on('ChannelDiscovery', function (event, response) {
        console.log('ChatRoomsCtrl: got event ChannelDiscovery');
        //if (!$scope.publicChannels || !$scope.publicChannels.length) {
        //  $scope.publicChannels = response.Channels;
        //}
        angular.merge($scope.publicChannels, $scope.publicChannels, response.Channels);
        $scope.$digest();
        //for (var i = 0; i < response.Channels.length; ++i) {
        //  $scope.publicChannels.push(response.Channel);
        //}
      });

      }]);
}(this.window));