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

        $http.post("/webChat/login", JSON.stringify($scope.user)).success(function (response) {
          console.log('RegisterCtrl: emitting event AuthSuccess');
          $scope.registerScreenHidden = true;
          MessageBus.send('AuthSuccess');
        });

      };
    }]).controller('ChatRoomsCtrl', ['$rootScope', '$scope', '$http', '$window', 'MessageBus', function ($rootScope, $scope, $http, $window, MessageBus) {

      $scope.chatRoomsScreenHidden = true;

      $scope.$on('AuthSuccess', function (event) {
        console.log('ChatRoomsCtrl: got event AuthSuccess');
        $scope.chatRoomsScreenHidden = false;
      });

      $scope.enterChannel = function (channelname) {
        console.log('in enterChannel(): Sending: ' + JSON.stringify(channelname));
        $http.post("/webChat/login", JSON.stringify($scope.user)).success(function (response) {
          console.log('enterChannel() success' + response);
        });
      };

      $scope.exitChannel = function () {
        console.log('in exitChannel()');
        $http.post("/webChat/login", JSON.stringify($scope.user)).success(function (response) {
          console.log('exitChannel() success' + response);
        });
      };

      function connect() {

        var wsUri = "ws://" + $window.location.host + "/WebSocketExample/chat/" + userInput.value;
        var websocket = new $window.WebSocket(wsUri);
        websocket.onopen = function (evt) {
          notify("Connected to Chat Server...");
        };
        websocket.onmessage = function (evt) {
          notify(evt.data);
        };
        websocket.onerror = function (evt) {
          notify('ERROR: ' + evt.data);
        };

        websocket.onclose = function (evt) {
          websocket = null;
        };

        connectBtn.hidden = true;
        sendBtn.hidden = false;
        logoutBtn.hidden = false;
        userInput.value = '';
      }

      function sendMessage() {
        if (websocket != null) {
          websocket.send(userInput.value);
        }
        userInput.value = '';
      }

      function notify(message) {
        var pre = document.createElement("p");
        pre.style.wordWrap = "break-word";
        pre.innerHTML = message;
        chatConsole.appendChild(pre);
      }

      function logout() {
        websocket.close();
        connectBtn.hidden = false;
        sendBtn.hidden = true;
        logoutBtn.hidden = true;
        userInput.value = '';
        notify("Logged out...");
      }


      $scope.channelThread = [{
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