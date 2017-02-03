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
        AvatarUrl: "img/default_avatar.png"
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
        if (data.Error === "Username does not exist") {
          $scope.authFailureWarningHidden = false;
          return;
        }
      });

      $scope.$on('AuthSuccess', function (event, data) {
        $scope.loginScreenHidden = true;
      });

    }]).controller('RegisterCtrl', ['$rootScope', '$scope', '$http', 'MessageBus', 'Servlets', function ($rootScope, $scope, $http, MessageBus, Servlets) {

      $scope.registerScreenHidden = true;

      $scope.$on('register', function (event, data) {
        //console.log('RegisterCtrl: got event register');
        $scope.registerScreenHidden = false;
      });

      $scope.register = function () {
        //console.log('in register(): Sending: ' + JSON.stringify($scope.user));
        $http.post("/webChat/register", JSON.stringify($scope.user)).success(function (response) {
          //console.log('RegisterCtrl: emitting event AuthSuccess');
          $scope.registerScreenHidden = true;
          MessageBus.send(response.MessageType, response);
        });

      };
    }]).controller('ChatRoomsCtrl', ['$rootScope', '$scope', '$http', '$window', 'MessageBus', 'Socket', 'Servlets', function ($rootScope, $scope, $http, $window, MessageBus, Socket, Servlets) {

      $scope.chatRoomsScreenHidden = true;
      $scope.currentChannel = {};
      $scope.currentChannelThread = {};
      $scope.subscribedChannels = [];
      $scope.publicChannels = [];
      $scope.expression = {};

      $scope.$on('AuthSuccess', function (event, response) {
        //console.log('ChatRoomsCtrl: got event AuthSuccess');
        $scope.chatRoomsScreenHidden = false;

        //get all channels and subscribed channels from json (must be here or on auth with servlet??)
        $rootScope.user = response.User;
        //console.log('in ChatRoomsCtrl:(): public channels:' + JSON.stringify(response.PublicChannels));
        $scope.publicChannels = response.PublicChannels;
        //console.log('in ChatRoomsCtrl:(): subscribed channels:' + JSON.stringify(response.SubscribedChannels));
        $scope.subscribedChannels = response.SubscribedChannels;
        Socket.connect();
      });

      $scope.$on('SubscribeSuccess', function (event, response) {
        //console.log('ChatRoomsCtrl: got event SubscribeSuccess');
        $scope.subscribedChannels.push(response.Channel);
      });

      $scope.$on('ChannelDiscovery', function (event, response) {
        //console.log('ChatRoomsCtrl: got event ChannelDiscovery');
        for (var i = 0; i < response.Channels.length; ++i) {
          $scope.publicChannels.push(response.Channel);
        }
      });

      var findChannel = function (channelName, channels) {
        //console.log('findChannelByName: entering ' + channelName);
        if (!channels) {
          return null;
        }
        for (var key = 0; key < channels.length; key++) {
          if (channels.hasOwnProperty(key)) {
            if (channels[key].ChannelName !== channelName) {
              continue;
            }
          }
          return {
            object: channels[key],
            index: key
          };
        }
        return null;
      };

      var getCurrentThread = function (channelName) {
        //console.log('getCurrentThread: entering ');
        return findChannel(channelName, $scope.subscribedChannels).object.ChannelThread;
      };

      $scope.subscribeToChannel = function (channelName) {
        // If channel already discovered, push to subscribed, if not, ask for subscription
        if (findChannel(channelName, $scope.publicChannels)) {
          $scope.subscribedChannels.push(findChannel(channelName, $scope.publicChannels).object);
          return;
        }

        var subscribeJson = {
          MessageType: {
            Subscribe: channelName,
            Username: $scope.user.Username
          }
        };
        //console.log('in subscribeToChannel(): Sending: ' + JSON.stringify(subscribeJson));
        Servlets.send("subscribe", subscribeJson);
      };

      $scope.enterChannel = function (channelName) {
        //console.log('in enterChannel(): Sending: ' + JSON.stringify(channelname));
        //console.log('in enterChannel(): $scope.currentChannel: ' + $scope.currentChannel);
        if (!findChannel(channelName, $scope.subscribedChannels)) {
          //console.log('enterChannel: no subscribed channels!');
          $scope.subscribeToChannel(channelName);
          //FIXME temporary
          //findChannel(channelName, $scope.subscribedChannels).object.ChannelThread = $scope.thread1;
          //if (findChannel(channelName, $scope.subscribedChannels).object.ChannelName === "auto") {
          //}
        }
        //findChannel(channelName, $scope.subscribedChannels).object.ChannelThread = $scope.thread1;
        $scope.currentChannelThread = getCurrentThread(channelName);
        $scope.currentChannel = findChannel(channelName, $scope.subscribedChannels).object;
        //console.log('in enterChannel(): Sending: ' + JSON.stringify($scope.currentChannelThread));
      };

      $scope.discoverChannels = function (query) {

        var queryJson = {
          MessageType: {
            ChannelDiscovery: query,
            Username: $scope.user.Username
          }
        };
        //console.log('in discoverChannels(): Sending: ' + JSON.stringify(subscribeJson));
        Servlets.send("discovery", queryJson);
      };

      $scope.unsubscribeChannel = function (channelName) {
        //console.log('in unsubscribeChannel()');
        var unsubscribeJson = {
          MessageType: {
            Unsubscribe: channelName,
            Username: $rootScope.user.Username
          }
        };
        //Socket.send(JSON.stringify(unsubscribeJson));
        //console.log('Deleting: ' + findChannel(channelname, $scope.subscribedChannels).index);
        $scope.subscribedChannels.splice(findChannel(channelName, $scope.subscribedChannels).index, 1);

        if ($scope.currentChannel === channelName) {
          $scope.currentChannel = {};
          $scope.currentChannelThread = {};
        }
      };

      $scope.isActive = function (channelName) {
        return $scope.currentChannel.ChannelName === channelName;
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