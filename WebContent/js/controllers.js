(function (global) {

  'use strict';
  /*global angular, console*/
  var WebChat = angular.module('webChat', ['constants', 'services', 'directives'])
    .controller('LoginCtrl', ['$rootScope', '$scope', '$http', 'MessageBus', function ($rootScope, $scope, $http, MessageBus) {

      $scope.user = {
        Username: "",
        Password: "",
        Nickname: "",
        Description: "",
        AvatarURL: ""
      };

      $scope.loginScreenHidden = false;

      $scope.login = function () {

        var credentials = {
          Username: $scope.user.Username,
          Password: $scope.user.Password
        };
        //console.log('in login(): Sending: ' + JSON.stringify(credentials));

        $http.post("/webChat/login", JSON.stringify(credentials)).success(function (response) {

          //console.log('in login(): Response:' + JSON.stringify(response));
          $scope.response = response;
          $scope.loginScreenHidden = true;
          //console.log('LoginCtrl: emitting event AuthSuccess');
          var jsonObjResponse = JSON.parse(response);
          MessageBus.send('AuthSuccess', jsonObjResponse.AuthSuccess);

        }).error(function (response) {
          console.log('in login(): Error response:' + JSON.stringify(response));
          $scope.loginScreenHidden = true;
          MessageBus.send('register');
        });

      };
      $scope.doregister = function () {
        console.log('in doregister(): Sending event register');
        $scope.loginScreenHidden = true;
        MessageBus.send('register');
      };

    }]).controller('RegisterCtrl', ['$rootScope', '$scope', '$http', 'MessageBus', function ($rootScope, $scope, $http, MessageBus) {

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
          var jsonObjResponse = JSON.parse(response);
          MessageBus.send('AuthSuccess', jsonObjResponse.AuthSuccess);
        });

      };
    }]).controller('ChatRoomsCtrl', ['$rootScope', '$scope', '$http', '$window', 'MessageBus', 'Socket', function ($rootScope, $scope, $http, $window, MessageBus, Socket) {

      $scope.chatRoomsScreenHidden = true;
      $scope.currentChannel = {};
      $scope.channelThread = {};
      $scope.expression = {};

      /* $scope.findChannel = function(searchValue) {     
    angular.forEach($scope.myData.SerialNumbers, function(value, key) {
        if (key === enteredValue) {
            $scope.results.push({serial: key, owner: value[0].Owner});
        }
    });
};*/
      // TEST 
      $scope.thread = [{
        Message: {
          ChannelID: "",
          User: {
            Nickname: "user1",
            Description: " that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
            AvatarURL: "css/img/default_avatar.png"
          },
          MessageTime: "2.01.2017, 10:52",
          ReplyToID: "",
          Content: "FDD111%6 Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long (for example if it has over 50 links inside of it)."
        },
        Replies: [{
          Message: {
            ChannelID: "",
            User: {
              Nickname: "user1",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
              AvatarURL: "css/img/default_avatar.png"
            },
            MessageTime: "2.01.2017, 10:05",
            ReplyToID: "",
            Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
          },
          Replies: [{
            Message: {
              ChannelID: "",
              User: {
                Nickname: "user1",
                Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
                AvatarURL: "css/img/default_avatar.png"
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
                AvatarURL: "css/img/default_avatar.png"
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
              AvatarURL: "css/img/default_avatar.png"
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
            AvatarURL: "css/img/default_avatar.png"
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
            AvatarURL: "css/img/default_avatar.png"
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
            AvatarURL: "css/img/default_avatar.png"
          },
          MessageTime: "2.01.2017, 10:05",
          ReplyToID: "",
          Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
        },
        Replies: []
      }];
      $scope.thread1 = [{
        Message: {
          ChannelID: "",
          User: {
            Nickname: "user1",
            Description: "FDFD that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
            AvatarURL: "css/img/default_avatar.png"
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
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
              AvatarURL: "css/img/default_avatar.png"
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
                AvatarURL: "css/img/default_avatar.png"
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
                AvatarURL: "css/img/default_avatar.png"
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
              AvatarURL: "css/img/default_avatar.png"
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
            AvatarURL: "css/img/default_avatar.png"
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
            AvatarURL: "css/img/default_avatar.png"
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
            AvatarURL: "css/img/default_avatar.png"
          },
          MessageTime: "2.01.2017, 10:05",
          ReplyToID: "",
          Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
        },
        Replies: []
      }];
      $scope.subscribedChannels = [];
      $scope.publicChannels = [{
          Name: "auto",
          ChannelThread: {},
          Users: [
            {
              Nickname: "user1",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top  .",
              AvatarURL: "css/img/default_avatar.png"
           }, {
              Nickname: "fgsdffdf123",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top o.",
              AvatarURL: "css/img/default_avatar.png"
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
              AvatarURL: "css/img/default_avatar.png"
           }, {
              Nickname: "fgsdffdf123",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top o.",
              AvatarURL: "css/img/default_avatar.png"
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
              AvatarURL: "css/img/default_avatar.png"
           }, {
              Nickname: "fgsdffdf123",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top o.",
              AvatarURL: "css/img/default_avatar.png"
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
              AvatarURL: "css/img/default_avatar.png"
           }, {
              Nickname: "fgsdffdf123",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top o.",
              AvatarURL: "css/img/default_avatar.png"
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
              AvatarURL: "css/img/default_avatar.png"
           }, {
              Nickname: "bltry4y4y45423",
              Description: "223523452345v23452v345v23",
              AvatarURL: "css/img/default_avatar.png"
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
              AvatarURL: "css/img/default_avatar.png"
           }, {
              Nickname: "blabla123",
              Description: "that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .",
              AvatarURL: "css/img/default_avatar.png"
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
              AvatarURL: "css/img/default_avatar.png"
           }, {
              Nickname: "b1212123",
              Description: "SDFGSDFGSDFGSDG2345",
              AvatarURL: "css/img/default_avatar.png"
           }
         ],
          Description: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long .",
          SubscribersCount: "344"
        }];
      //TEST

      $scope.$on('AuthSuccess', function (event, response) {
        //console.log('ChatRoomsCtrl: got event AuthSuccess');
        $scope.chatRoomsScreenHidden = false;

        //get all channels and subscribed channels from json (must be here or on auth with servlet??)
        $scope.user.Nickname = response.User.Nickname;
        $scope.user.AvatarURL = response.User.AvatarURL;
        $scope.user.Description = response.User.Description;
        $scope.publicChannels = response.PublicChannels;
        $scope.subscribedChannels = response.SubcribedChannels;
        Socket.connect();
      });

      var findChannel = function (channelName, channels) {
        //console.log('findChannelByName: entering ' + channelName);
        if (!channels) {
          return null;
        }
        for (var key = 0; key < channels.length; key++) {
          if (channels.hasOwnProperty(key)) {
            if (channels[key].Name !== channelName) {
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
        if (!findChannel(channelName, $scope.subscribedChannels)) {
          //console.log('getCurrentThread: no subscribed channels!');
          $scope.subscribedChannels.push(findChannel(channelName, $scope.publicChannels).object);

          findChannel(channelName, $scope.subscribedChannels).object.ChannelThread = $scope.thread1;
          //if (findChannel(channelName, $scope.subscribedChannels).object.Name === "auto") {
          //  findChannel(channelName, $scope.subscribedChannels).object.ChannelThread = $scope.thread;
          //}

          var subscribeJson = {
            Subscribe: channelName
          };
          // FIXME code to ask server for channel thread
          //Socket.send(JSON.stringify(subscribeJson));
        }
        return findChannel(channelName, $scope.subscribedChannels).object.ChannelThread;
      };

      $scope.enterChannel = function (channelName) {
        //console.log('in enterChannel(): Sending: ' + JSON.stringify(channelname));
        //console.log('in enterChannel(): $scope.currentChannel: ' + $scope.currentChannel);
        $scope.channelThread = getCurrentThread(channelName);
        $scope.currentChannel = findChannel(channelName, $scope.subscribedChannels).object;
        //console.log('in enterChannel(): Sending: ' + JSON.stringify($scope.channelThread));
      };

      $scope.unsubscribeChannel = function (channelname) {
        //console.log('in unsubscribeChannel()');
        var unsubscribeJson = {
          Unsubscribe: channelname
        };
        //Socket.send(JSON.stringify(unsubscribeJson));
        //console.log('Deleting: ' + findChannel(channelname, $scope.subscribedChannels).index);
        $scope.subscribedChannels.splice(findChannel(channelname, $scope.subscribedChannels).index, 1);

        $scope.currentChannel = {};
        $scope.channelThread = {};
      };

      $scope.isActive = function (channelname) {
        return $scope.currentChannel.Name === channelname;
      };

      $scope.searchChannel = function (channel) {
        if (!$scope.query || (channel.Name.toLowerCase().indexOf($scope.query.toLowerCase()) != -1)) {
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