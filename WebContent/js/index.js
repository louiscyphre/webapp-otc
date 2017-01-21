(function (global) {

  'use strict';
  /*global angular, console*/
  var WebChat = angular.module('webChat', ['services', 'directives'])
    .controller('LoginCtrl', ['$rootScope', '$scope', '$http', 'MessageBus', function ($rootScope, $scope, $http, MessageBus) {

      $scope.user = {
        Username: "",
        Password: "",
        Nickname: "",
        Description: "",
        AvatarUrl: ""
      };

      $scope.loginScreenHidden = false;

      $scope.login = function () {

        var credentials = {
          Username: $scope.user.Username,
          Password: $scope.user.Password
        };

        console.log('in login(): Sending: ' + JSON.stringify(credentials));

        $http.post("/webChat/login", JSON.stringify(credentials)).success(function (response) {

          console.log('in login(): Response:' + JSON.stringify(response));

          $scope.response = response;
          $scope.loginScreenHidden = true;

          console.log('LoginCtrl: emitting event AuthSuccess');
          MessageBus.send('AuthSuccess', response);

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
        console.log('RegisterCtrl: got event register');
        $scope.registerScreenHidden = false;
      });

      $scope.register = function () {
        console.log('in register(): Sending: ' + JSON.stringify($scope.user));

        $http.post("/webChat/register", JSON.stringify($scope.user)).success(function (response) {
          console.log('RegisterCtrl: emitting event AuthSuccess');
          $scope.registerScreenHidden = true;
          MessageBus.send('AuthSuccess');
        });

      };
    }]).controller('ChatRoomsCtrl', ['$rootScope', '$scope', '$http', '$window', 'MessageBus', function ($rootScope, $scope, $http, $window, MessageBus) {

      $scope.chatRoomsScreenHidden = true;
      $scope.currentChannel = {};
      $scope.channelThread = {};

      // TEST 
      $scope.thread = [{
        Message: {
          ChannelID: "",
          UserID: "AAA",
          MessageTime: "2.01.2017, 10:52",
          ReplyToID: "",
          Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long (for example if it has over 50 links inside of it)."
        },
        Replies: [{
          Message: {
            ChannelID: "",
            UserID: "WWWWWOOOAA",
            MessageTime: "2.01.2017, 10:05",
            ReplyToID: "",
            Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
          },
          Replies: [{
            Message: {
              ChannelID: "",
              UserID: "AAA",
              MessageTime: "2.01.2017, 10:52",
              ReplyToID: "",
              Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
            },
            Replies: []
          }, {
            Message: {
              ChannelID: "",
              UserID: "AAA",
              MessageTime: "2.01.2017, 10:52",
              ReplyToID: "",
              Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
            },
            Replies: []
          }]
        }, {
          Message: {
            ChannelID: "",
            UserID: "WWWWWOOOAA",
            MessageTime: "2.01.2017, 10:05",
            ReplyToID: "",
            Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
          },
          Replies: []
        }]
      }, {
        Message: {
          ChannelID: "",
          UserID: "WWWWWOOOAA",
          MessageTime: "2.01.2017, 10:05",
          ReplyToID: "",
          Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
        },
        Replies: []
      }, {
        Message: {
          ChannelID: "",
          UserID: "WWWWWOOOAA",
          MessageTime: "2.01.2017, 10:05",
          ReplyToID: "",
          Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
        },
        Replies: []
      }, {
        Message: {
          ChannelID: "",
          UserID: "WWWWWOOOAA",
          MessageTime: "2.01.2017, 10:05",
          ReplyToID: "",
          Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
        },
        Replies: []
      }];
      $scope.thread1 = [{
        Message: {
          ChannelID: "",
          UserID: "sdfsdfs",
          MessageTime: "2.01.2017, 10:52",
          ReplyToID: "",
          Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on t"
        },
        Replies: [{
          Message: {
            ChannelID: "",
            UserID: "sdfsdf",
            MessageTime: "2.01.2017, 10:05",
            ReplyToID: "",
            Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit ."
          },
          Replies: [{
            Message: {
              ChannelID: "",
              UserID: "fghfh",
              MessageTime: "2.01.2017, 10:52",
              ReplyToID: "",
              Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
            },
            Replies: []
          }, {
            Message: {
              ChannelID: "",
              UserID: "fhfghgfhfh",
              MessageTime: "2.01.2017, 10:52",
              ReplyToID: "",
              Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
            },
            Replies: []
          }]
        }, {
          Message: {
            ChannelID: "",
            UserID: "WWWWWOOOAA",
            MessageTime: "2.01.2017, 10:05",
            ReplyToID: "",
            Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
          },
          Replies: []
        }]
      }, {
        Message: {
          ChannelID: "",
          UserID: "WWWWWOOOAA",
          MessageTime: "2.01.2017, 10:05",
          ReplyToID: "",
          Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
        },
        Replies: []
      }, {
        Message: {
          ChannelID: "",
          UserID: "WWWWWOOOAA",
          MessageTime: "2.01.2017, 10:05",
          ReplyToID: "",
          Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
        },
        Replies: []
      }, {
        Message: {
          ChannelID: "",
          UserID: "WWWWWOOOAA",
          MessageTime: "2.01.2017, 10:05",
          ReplyToID: "",
          Content: "Notice that this div element has a left margin of 25%. This is because the side navigation is set to 25% width. If you remove the margin, the sidenav will overlay/sit on top of this div.Also notice that we have set overflow:auto to sidenav. This will add a scrollbar when the sidenav is too long ."
        },
        Replies: []
      }];
      $scope.subscribedChannels = [];
      /*[{
              ChannelID: "channel1",
              ChannelThread: $scope.thread,
              SubscribersCount: "10"
            }, {
              Name: "channel2",
              ChannelThread: $scope.thread,
              Description: "fasdkjfhajfh",
              SubscribersCount: "19"
            }, {
              ChannelID: "channel3",
              ChannelThread: $scope.thread1,
              SubscribersCount: "1"
            }, {
              ChannelID: "channel4",
              ChannelThread: $scope.thread1,
              SubscribersCount: "100"
            }, {
              ChannelID: "channel5",
              ChannelThread: $scope.thread1,
              SubscribersCount: "15"
            }];*/
      $scope.publicChannels = [{
          Name: "auto",
          ChannelThread: {},
          Description: "fasdkjfhajfh",
          SubscribersCount: "20"
        },
        {
          Name: "ooollolo",
          ChannelThread: {},
          Description: "fasXfzFZFajfh",
          SubscribersCount: "234"
        },
        {
          Name: "oqwqwlolo",
          ChannelThread: {},
          Description: "fasqwqwqFZFajfh",
          SubscribersCount: "4344"
        },
        {
          Name: "213412312",
          ChannelThread: {},
          Description: "WEWjfh",
          SubscribersCount: "344"
        }];
      //TEST

      $scope.$on('AuthSuccess', function (event) {
        console.log('ChatRoomsCtrl: got event AuthSuccess');
        $scope.chatRoomsScreenHidden = false;
        // connect websocket
        //get all channels and subscribed channels json
      });

      var getCurrentThread = function (channelName) {
        //console.log('getCurrentThread: entering');
        if (!findChannelByName(channelName, $scope.subscribedChannels)) {
          //console.log('getCurrentThread: no subscribed channels!');
          $scope.subscribedChannels.push(findChannelByName(channelName, $scope.publicChannels));
          findChannelByName(channelName, $scope.subscribedChannels).ChannelThread = $scope.thread;
          // FIXME code to ask server for channel thread
        }
        return findChannelByName(channelName, $scope.subscribedChannels).ChannelThread;
      };


      var findChannelByName = function (channelName, channels) {
        //console.log('findChannelByName: entering ' + channelName);
        if (!channels) {
          return null;
        }
        for (var key in channels) {
          if (channels.hasOwnProperty(key)) {
            if (channels[key].Name !== channelName) {
              continue;
            }
          }
          return channels[key];
        }
        return null;
      };

      $scope.enterChannel = function (channelname) {
        //console.log('in enterChannel(): Sending: ' + JSON.stringify(channelname));
        //console.log('in enterChannel(): $scope.currentChannel: ' + $scope.currentChannel);
        $scope.channelThread = getCurrentThread(channelname);
        $scope.currentChannel = channelname;
        //console.log('in enterChannel(): Sending: ' + JSON.stringify($scope.channelThread));
      };

      $scope.exitChannel = function () {
        console.log('in exitChannel()');
        $scope.currentChannel = {};
        $scope.channelThread = {};
      };

      $scope.isActive = function (channelname) {
        return $scope.currentChannel === channelname;
      };
      }]);
}(this.window));

//console.log('in enterchat(): Sending: ' + JSON.stringify($scope.myTree));

/*$scope.showThread = function (jsonTree) {
  //console.log('in showthread(): Sending: ' + JSON.stringify($scope.myTree));
  var retStr = '<li>' + '<p>' + jsonTree.post.data + '</p>',
    childIndex = 0;
  if ((jsonTree.replies) && (jsonTree.replies.length > 0)) {
    retStr += '<ul>';
    for (childIndex = 0; childIndex <= jsonTree.replies.length - 1; childIndex += 1) {
      retStr += $scope.showThread(jsonTree.replies[childIndex]);
    }
    retStr += '</ul>';
  }
  retStr += '</li>';
  return $sce.trustAsHtml(retStr);
};*/