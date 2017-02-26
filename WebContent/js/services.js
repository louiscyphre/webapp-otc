(function (global) {

  'use strict';
  /*global angular, console*/
  var services = angular.module('services', []);

  services.service('messageBus', ['$rootScope', function ($rootScope) {
    // application is event based.
    // this service is used to broadcast various events to application
    return {
      send: function (evt, data) {
        $rootScope.$broadcast(evt, data);
      }
    };
  }]).service('servlets', ['$rootScope', '$http', 'messageBus', function ($rootScope, $http, messageBus) {
    // this service used to connect to login and register servlets.
    return {
      send: function (pathToServlet, jsonToSend) {
        $http.post("/webapp/" + pathToServlet, JSON.stringify(jsonToSend)).success(function (response) {
          messageBus.send(response.messageType, response);

        }).error(function (response) {
          messageBus.send(response.messageType, response);
        });
      }
    };
   }]).service('socket', ['$window', 'messageBus', function ($window, messageBus) {
    // singleton service to open socket to server.
    var webSocket = null;
    return {
      connect: function connect(username) {
        if (webSocket) {
          return;
        }
        var wsUri = "ws://" + $window.location.host + "/webapp/" + username;
        webSocket = new $window.WebSocket(wsUri);

        webSocket.onopen = function (evt) {};
        webSocket.onmessage = function (evt) {
          // send .messageType event to application using messageBus service.
          // see protocol about types and structure of all messages.
          messageBus.send(JSON.parse(evt.data).messageType, JSON.parse(evt.data));
        };
        webSocket.onerror = function (evt) {
          messageBus.send(JSON.parse(evt.data).messageType, JSON.parse(evt.data));
        };

        webSocket.onclose = function (evt) {
          webSocket = null;
        };
      },
      // send prepared json to server as json string
      send: function send(jSonObect) {
        if (webSocket !== null) {
          webSocket.send(JSON.stringify(jSonObect));
        }
        jSonObect = {};
      },
      // log off chat, close socket
      logout: function logout() {
        webSocket.close();
      }
    };
   }]);
}(this.window));