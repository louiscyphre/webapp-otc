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

      $scope.currentChannel = {};
      $scope.currentChannelThread = {};
      $scope.subscribedChannels = [];
      $scope.publicChannels = [];
      $scope.privateChannels = [];
      $scope.expression = {};

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

      var appendToThreadById = function (replyMessage, thread) {
        for (var message in thread) {
          if (replyMessage.ReplyToId === message.Id) {
            message.Replies.push(replyMessage);
            return;
          }
          if (message.Replies.length > 0) {
            appendToThreadById(replyMessage, message.Replies);
          }
        }
      };

      var getCurrentThread = function (channelName, channelsList) {
        //console.log('getCurrentThread: entering ');
        return findChannel(channelName, channelsList).object.ChannelThread;
      };

      $scope.subscribeToChannel = function (channelName) {
        // If channel already discovered, push to subscribed, if not, ask for subscription
        if (findChannel(channelName, $scope.publicChannels)) {
          $scope.subscribedChannels.push(findChannel(channelName, $scope.publicChannels).object);
          return;
        }
        var subscribeJson = {
          MessageType: "Subscribe",
          MessageContent: {
            ChannelId: channelName,
          }
        };
        //console.log('in subscribeToChannel(): Sending: ' + JSON.stringify(subscribeJson));
        Socket.send(subscribeJson);
      };

      $scope.enterChannel = function (channelName) {
        //console.log('in enterChannel(): channelName: ' + JSON.stringify(channelname));
        //console.log('in enterChannel(): $scope.currentChannel: ' + $scope.currentChannel);
        if (!findChannel(channelName, $scope.subscribedChannels)) {
          //console.log('enterChannel: no subscribed channels!');
          $scope.subscribeToChannel(channelName);
        }
        $scope.currentChannelThread = getCurrentThread(channelName, $scope.subscribedChannels);
        $scope.currentChannel = findChannel(channelName, $scope.subscribedChannels).object;
        $scope.downloadMessages(channelName);
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
        var channelName = $scope.user.Username + targetUsername;
        console.log('in enterPivateChannel(): entering: ' + JSON.stringify(channelName));
        if (!findChannel(channelName, $scope.privateChannels)) {
          console.log('enterPrivateChannel: no private channels!');
          var description = "Private channel for " + $scope.user.Nickname + " and " + targetNickname + ", created by " + $scope.user.Nickname;
          $scope.createChannel(channelName, description, $scope.user.Username);
          return;
        }
        $scope.currentChannelThread = getCurrentThread(channelName, $scope.privateChannels);
        $scope.currentChannel = findChannel(channelName, $scope.privateChannels).object;
        $scope.downloadMessages(channelName);
      };

      $scope.downloadMessages = function (channelName) {

        var downloadMessagesJson = {
          MessageType: "DownloadMessages",
          MessageContent: {
            Channel: channelName,
          }
        };
        console.log('in downloadMessages(): Sending: ' + JSON.stringify(downloadMessagesJson));
        Socket.send(downloadMessagesJson);
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

      $scope.isActive = function (channelName) {
        return $scope.currentChannel.ChannelName === channelName;
      };

      $scope.getPrivateChannelName = function (channelName) {
        var privateChannelNameToShow = "";
        var channel = findChannel(channelName, $scope.privateChannels).object;
        for (var i = 0; i < channel.Users.length; ++i) {
          if (channel.Users[i].Username.toLowerCase().indexOf($scope.user.Username) != -1) {
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
        $scope.publicChannels = response.PublicChannels;
        //console.log('in ChatRoomsCtrl:(): subscribed channels:' + JSON.stringify(response.SubscribedChannels));
        $scope.subscribedChannels = response.SubscribedChannels;
        $scope.privateChannels = response.PrivateChannels;

        Socket.connect($rootScope.user.Username);
      });

      $scope.$on('SubscribeSuccess', function (event, response) {
        //console.log('ChatRoomsCtrl: got event SubscribeSuccess');
        $scope.subscribedChannels.push(response.Channel);
      });

      $scope.$on('ChannelSuccess', function (event, response) {
        //console.log('ChatRoomsCtrl: got event ChannelSuccess');
        var channels;
        if (response.Channel.IsPublic) {
          channels = $scope.subscribedChannels;
        } else {
          channels = $scope.privateChannels;
        }
        channels.push(response.Channel);

        $scope.currentChannelThread = getCurrentThread(response.Channel, channels);
        $scope.currentChannel = findChannel(response.Channel, channels).object;
      });

      $scope.$on('UserSubscribed', function (event, response) {
        //console.log('ChatRoomsCtrl: got event UserSubscribed');
        var channel = findChannel(response.Channel, $scope.subscribedChannels);
        if (!channel) {
          return;
        }
        channel.Users.push(response.User.Username);
      });

      $scope.$on('UserUnsubscribed', function (event, response) {
        //console.log('ChatRoomsCtrl: got event UserUnsubscribed');
        var channel = findChannel(response.Channel, $scope.subscribedChannels);
        if (!channel) {
          return;
        }
        channel.Users.pop(response.Username);
      });

      $scope.$on('Unsubscribe', function (event, response) {
        //console.log('ChatRoomsCtrl: got event Unsubscribe');
        //console.log('Deleting: ' + findChannel(channelname, $scope.subscribedChannels).index);
        $scope.subscribedChannels.splice(findChannel(response.Channel, $scope.subscribedChannels).index, 1);

        if ($scope.currentChannel === response.Channel) {
          $scope.currentChannel = {};
          $scope.currentChannelThread = {};
        }
      });

      $scope.$on('DownloadMessage', function (event, response) {
        //console.log('ChatRoomsCtrl: got event DownloadMessage');
        if (!findChannel(response.Channel, $scope.subscribedChannels) || !response.ChannelThread.lengh) {
          return;
        }
        var thread = findChannel(response.Channel, $scope.subscribedChannels).object.ChannelThread;

        if (response.ChannelThread[0].Message.ReplyToId !== -1) {
          appendToThreadById(response.ChannelThread[0].Message, thread);
          response.ChannelThread.splice(0, 1);
        }
        thread.concat(response.ChannelThread);
      });

      $scope.$on('ChannelDiscovery', function (event, response) {
        //console.log('ChatRoomsCtrl: got event ChannelDiscovery');
        for (var i = 0; i < response.Channels.length; ++i) {
          $scope.publicChannels.push(response.Channel);
        }
      });

      }]);
}(this.window));

/*[{
          Name: "auto",
          ChannelThread: {},
          Users: [
            {
              Nickname: "user1",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
              AvatarUrl: "css/img/default_avatar.png"
           }, {
              Nickname: "fgsdffdf123",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top o.",
              AvatarUrl: "css/img/default_avatar.png"
           }
         ],
          Description: " WWWW QQADDrt",
          SubscribersCount: "20"
        }, {
          Name: "auto0",
          ChannelThread: {},
          Users: [
            {
              Nickname: "user1",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
              AvatarUrl: "css/img/default_avatar.png"
           }, {
              Nickname: "fgsdffdf123",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top o.",
              AvatarUrl: "css/img/default_avatar.png"
           }
         ],
          Description: " WWWW QQADDrt",
          SubscribersCount: "20"
        }, {
          Name: "auto1",
          ChannelThread: {},
          Users: [
            {
              Nickname: "user1",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
              AvatarUrl: "css/img/default_avatar.png"
           }, {
              Nickname: "fgsdffdf123",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top o.",
              AvatarUrl: "css/img/default_avatar.png"
           }
         ],
          Description: " WWWW QQADDrt",
          SubscribersCount: "20"
        }, {
          Name: "auto2",
          ChannelThread: {},
          Users: [
            {
              Nickname: "user1",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
              AvatarUrl: "css/img/default_avatar.png"
           }, {
              Nickname: "fgsdffdf123",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top o.",
              AvatarUrl: "css/img/default_avatar.png"
           }
         ],
          Description: " WWWW QQADDrt",
          SubscribersCount: "20"
        },
        {
          Name: "ooollolo",
          ChannelThread: {},
          Users: [
            {
              Nickname: "blabutru1",
              Description: "234523452 2 324523v52345v23523",
              AvatarUrl: "css/img/default_avatar.png"
           }, {
              Nickname: "bltry4y4y45423",
              Description: "223523452345v23452v345v23",
              AvatarUrl: "css/img/default_avatar.png"
           }
         ],
          Description: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .",
          SubscribersCount: "234"
        },
        {
          Name: "oqwqwlolo",
          ChannelThread: {},
          Users: [
            {
              Nickname: "blabla1",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .",
              AvatarUrl: "css/img/default_avatar.png"
           }, {
              Nickname: "blabla123",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .",
              AvatarUrl: "css/img/default_avatar.png"
           }
         ],
          Description: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .",
          SubscribersCount: "4344"
        },
        {
          Name: "213412312",
          ChannelThread: {},
          Users: [
            {
              Nickname: "bla1212a1",
              Description: "SDFGSDGHSDGSDFGs",
              AvatarUrl: "css/img/default_avatar.png"
           }, {
              Nickname: "b1212123",
              Description: "SDFGSDFGSDFGSDG2345",
              AvatarUrl: "css/img/default_avatar.png"
           }
         ],
          Description: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .",
          SubscribersCount: "344"
        }];*/
//TEST
/* TEST 
$scope.thread = [{
  Message: {
    ChannelID: "",
    User: {
      Nickname: "user1",
      Description: " that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
      AvatarUrl: "css/img/default_avatar.png"
    },
    MessageTime: "2.01.2017, 10:52",
    ReplyToID: "",
    Content: "FDD111%6 Notice ."
  },
  Replies: [{
    Message: {
      ChannelID: "",
      User: {
        Nickname: "user1",
        Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
        AvatarUrl: "css/img/default_avatar.png"
      },
      MessageTime: "2.01.2017, 10:05",
      ReplyToID: "",
      Content: "Notice that "
    },
    Replies: [{
      Message: {
        ChannelID: "",
        User: {
          Nickname: "user1",
          Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
          AvatarUrl: "css/img/default_avatar.png"
        },
        MessageTime: "2.01.2017, 10:52",
        ReplyToID: "",
        Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
      },
      Replies: []
    }, {
      Message: {
        ChannelID: "",
        User: {
          Nickname: "user1",
          Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
          AvatarUrl: "css/img/default_avatar.png"
        },
        MessageTime: "2.01.2017, 10:52",
        ReplyToID: "",
        Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
      },
      Replies: []
    }]
  }, {
    Message: {
      ChannelID: "",
      User: {
        Nickname: "user1",
        Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
        AvatarUrl: "css/img/default_avatar.png"
      },
      MessageTime: "2.01.2017, 10:05",
      ReplyToID: "",
      Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
    },
    Replies: []
  }]
}, {
  Message: {
    ChannelID: "",
    User: {
      Nickname: "user1",
      Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
      AvatarUrl: "css/img/default_avatar.png"
    },
    MessageTime: "2.01.2017, 10:05",
    ReplyToID: "",
    Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
  },
  Replies: []
}, {
  Message: {
    ChannelID: "",
    User: {
      Nickname: "user1",
      Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
      AvatarUrl: "css/img/default_avatar.png"
    },
    MessageTime: "2.01.2017, 10:05",
    ReplyToID: "",
    Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
  },
  Replies: []
}, {
  Message: {
    ChannelID: "",
    User: {
      Nickname: "user1",
      Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
      AvatarUrl: "css/img/default_avatar.png"
    },
    MessageTime: "2.01.2017, 10:05",
    ReplyToID: "",
    Content: "Notice that this d ."
  },
  Replies: []
}];*/
/*
      $scope.thread1 = [{
        Message: {
          ChannelID: "",
          User: {
            Nickname: "user1",
            Description: "FDFD that this div elem  .",
            AvatarUrl: "css/img/default_avatar.png"
          },
          MessageTime: "2.01.2017, 10:52",
          ReplyToID: "",
          Content: "DVDVD Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on t"
        },
        Replies: [{
          Message: {
            ChannelID: "",
            User: {
              Nickname: "user1",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that  .",
              AvatarUrl: "css/img/default_avatar.png"
            },
            MessageTime: "2.01.2017, 10:05",
            ReplyToID: "",
            Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit ."
          },
          Replies: [{
            Message: {
              ChannelID: "",
              User: {
                Nickname: "user1",
                Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
                AvatarUrl: "css/img/default_avatar.png"
              },
              MessageTime: "2.01.2017, 10:52",
              ReplyToID: "",
              Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
            },
            Replies: []
                }, {
            Message: {
              ChannelID: "",
              User: {
                Nickname: "user1",
                Description: "FDFD that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
                AvatarUrl: "css/img/default_avatar.png"
              },
              MessageTime: "2.01.2017, 10:52",
              ReplyToID: "",
              Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
            },
            Replies: []
                }]
              }, {
          Message: {
            ChannelID: "",
            User: {
              Nickname: "user1",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
              AvatarUrl: "css/img/default_avatar.png"
            },
            MessageTime: "2.01.2017, 10:05",
            ReplyToID: "",
            Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
          },
          Replies: []
              }]
            }, {
        Message: {
          ChannelID: "",
          User: {
            Nickname: "user1",
            Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
            AvatarUrl: "css/img/default_avatar.png"
          },
          MessageTime: "2.01.2017, 10:05",
          ReplyToID: "",
          Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
        },
        Replies: []
            }, {
        Message: {
          ChannelID: "",
          User: {
            Nickname: "user1",
            Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
            AvatarUrl: "css/img/default_avatar.png"
          },
          MessageTime: "2.01.2017, 10:05",
          ReplyToID: "",
          Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
        },
        Replies: []
            }, {
        Message: {
          ChannelID: "",
          User: {
            Nickname: "user1",
            Description: " @user1 that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
            AvatarUrl: "css/img/default_avatar.png"
          },
          MessageTime: "2.01.2017, 10:05",
          ReplyToID: "",
          Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
        },
        Replies: []
            }];*/