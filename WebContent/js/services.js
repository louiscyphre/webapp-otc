(function (global) {

  'use strict';
  /*global angular, console*/
  var services = angular.module('services', []);

  services.service('MessageBus', ['$rootScope', function ($rootScope) {
    // Application is event based.
    // This service is used to broadcast various events to application
    return {
      send: function (evt, data) {
        console.log('in MessageBus.send(): response:' + JSON.stringify(data));
        $rootScope.$broadcast(evt, data);
      }
    };
  }]).service('Servlets', ['$rootScope', '$http', 'MessageBus', function ($rootScope, $http, MessageBus) {
    // This service used to connect to login and register servlets.
    return {
      send: function (pathToServlet, jsonToSend) {
        $http.post("/webChat/" + pathToServlet, JSON.stringify(jsonToSend)).success(function (response) {
          //console.log('Servlets.send.success(): emitting event ' + JSON.stringify(response.MessageType));
          MessageBus.send(response.MessageType, response);

        }).error(function (response) {
          //console.log('in Servlets.send.error(): Error response:' + JSON.stringify(response));
          MessageBus.send(response.MessageType, response);
        });
      }
    };
   }]).service('Socket', ['$window', 'MessageBus', function ($window, MessageBus) {
    // Singleton service to open socket to server.
    var websocket = null;
    return {
      connect: function connect(username) {

        //console.log('Socket.connect(): connecting a socket');
        if (websocket) {
          return;
        }
        var wsUri = "ws://" + $window.location.host + "/webChat/" + username;
        websocket = new $window.WebSocket(wsUri);

        websocket.onopen = function (evt) {
          //console.log('in websocket.onopen(): opened socket to server');
        };
        websocket.onmessage = function (evt) {
          // Send .MessageType event to application using MessageBus service.
          // See protocol about types and structure of all messages.
          MessageBus.send(JSON.parse(evt.data).MessageType, JSON.parse(evt.data));
        };
        websocket.onerror = function (evt) {
          //console.log('in websocket.onerror(): Error on server:');
          MessageBus.send(JSON.parse(evt.data).MessageType, JSON.parse(evt.data));
        };

        websocket.onclose = function (evt) {
          websocket = null;
        };
      },
      // Send prepared json to server as json string
      send: function send(jSonObect) {
        if (websocket !== null) {
          //console.log('in websocket.send(): sending request:' + JSON.stringify(jSonObect));
          websocket.send(JSON.stringify(jSonObect));
        }
        jSonObect = {};
      },
      // Log off chat, close socket
      logout: function logout() {
        websocket.close();
      }
    };
   }]);
}(this.window));