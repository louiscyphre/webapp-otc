(function (global) {

  'use strict';
  /*global angular, console*/
  var WebChat = angular.module('webChat', [])
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
          console.log('LoginCtrl: emitting event loginSuccess');

          MessageBus.send('loginSuccess', response);

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

          console.log('Register success' + response);
          $scope.registerScreenHidden = true;
          MessageBus.send('registerSuccess');
        });

      };
    }]).controller('ChatRoomsCtrl', ['$rootScope', '$scope', '$http', 'MessageBus', function ($rootScope, $scope, $http, MessageBus) {

      $scope.chatRoomsScreenHidden = true;

      $scope.$on('loginSuccess', function (event) {
        console.log('ChatRoomsCtrl: got event loginSuccess');
        $scope.chatRoomsScreenHidden = false;
      });
      $scope.$on('loginSuccess', function (event) {
        console.log('ChatRoomsCtrl: got event registerSuccess');
        $scope.chatRoomsScreenHidden = false;
      });

      $scope.enterchat = function () {
        console.log('in enterchat(): Sending: ' + JSON.stringify($scope.user));

        $http.post("/webChat/login", JSON.stringify($scope.user)).success(function (response) {

          console.log('Register success' + response);
        });

      };
    }]);

  WebChat.factory('MessageBus', ['$rootScope', function ($rootScope) {

    return {
      send: function (msg, data) {
        $rootScope.$broadcast(msg, data);
      }
    };
  }]);

}(this.window));
//this method will be called upon change in the text typed by the user in the searchbox
/*$scope.search =
  function () {
    if (!$scope.query || $scope.query.length == 0) {
      //initially we show all table data
      $scope.result = $scope.records;
    } else {
      var qstr = $scope.query.toLowerCase();
      $scope.result = [];
      for (x in $scope.records) {
        //check for a match (up to a lowercasing difference)
        if ($scope.records[x].Name.toLowerCase().match(qstr) ||
          $scope.records[x].City.toLowerCase().match(qstr) ||
          $scope.records[x].Country.toLowerCase().match(qstr)) {
          $scope.result.push($scope.records[x]); //add record to search result
        }
      }
    }
  };

//delegate the text highlighting task to an external helper service 
$scope.hlight = function (text, qstr) {
  return highlightText.highlight(text, qstr);
};*/