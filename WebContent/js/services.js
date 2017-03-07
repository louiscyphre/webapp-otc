/*
 *     webapp-otc - an online collaboration tool .
 *     Copyright (C) 2017 Ilia Butvinnik and Michael Goldman
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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