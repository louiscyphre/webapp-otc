(function (global) {

  'use strict';
  /*global angular, console*/
  var services = angular.module('services', ['constants']);

  services.service('MessageBus', ['$rootScope', function ($rootScope) {

    return {
      send: function (evt, data) {
        console.log('in MessageBus.send(): response:' + JSON.stringify(data));
        $rootScope.$broadcast(evt, data);
      }
    };
  }]).service('Servlets', ['$rootScope', '$http', 'MessageBus', function ($rootScope, $http, MessageBus) {

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
   }]).factory('Socket', ['$window', 'MessageBus', function ($window, MessageBus) {

    var websocket = null;
    return {
      connect: function connect(username) {

        console.log('Socket.connect(): connecting a socket');
        if (websocket) {
          return;
        }
        var wsUri = "ws://" + $window.location.host + "/webChat/" + username;
        websocket = new $window.WebSocket(wsUri);

        websocket.onopen = function (evt) {
          console.log('in websocket.onopen(): opened socket to server');
        };
        websocket.onmessage = function (evt) {
          // Send event using first property name of jSon object, and attach
          // data, so listener can catch it
          MessageBus.send("'" + Object.keys(evt.data) + "'", JSON.parse(evt.data));
        };
        websocket.onerror = function (evt) {
          console.log('in websocket.onopen(): Error on server:');
          MessageBus.send("'" + Object.keys(evt.data) + "'", JSON.parse(evt.data));
        };

        websocket.onclose = function (evt) {
          websocket = null;
        };
      },

      send: function send(jSonObect) {
        if (websocket !== null) {
          websocket.send(JSON.stringify(jSonObect));
        }
        jSonObect = {};
      },

      logout: function logout() {
        websocket.close();
      }
    };
   }]);
  /*.factory('highlightText', ['$sce', function ($sce) {

      //service method to be called upon text highlighting
      var highlight = function (text, qstr) {

        if (!qstr || qstr.length === 0) {
          return $sce.trustAsHtml(text);
        }

        var lcqstr = qstr.toLowerCase(),
          lctext = text.toLowerCase(),
          i = lctext.indexOf(lcqstr),
          prfxStr = {},
          match = {},
          sfxStr = {},
          hgltText = {};

        if (i >= 0) {

          prfxStr = text.substring(0, i);
          match = text.substring(i, i + qstr.length);
          sfxStr = text.substring(i + qstr.length, text.length);
          //we wrap the matching text with <mark>...</mark> (bootstrap element) that highlights the matching string
          hgltText = prfxStr + '<mark>' + match + '<\/mark>' + sfxStr;
          return $sce.trustAsHtml(hgltText);
        } else {
          return $sce.trustAsHtml(text);
        }
      };

      return {
        highlight: highlight
      };

    }]);*/
  /*.factory('ShowThreadService', ['$scope', '$sce', function ($scope, $sce) {

      var listFromJsonTree = function (jsonTree) {
        var retStr = '<li>' + jsonTree.post.name,
          childIndex = 0;
        if ((jsonTree.replies) && (jsonTree.replies.length > 0)) {
          retStr += '<ul>';
          for (childIndex = 0; childIndex <= jsonTree.replies.length - 1; childIndex += 1) {
            retStr += $scope.listFromJsonTree(jsonTree.replies[childIndex]);
          }
          retStr += '</ul>';
        }
        retStr += '</li>';
        return $sce.trustAsHtml(retStr);
      };
      return {
        listFromJsonTree: listFromJsonTree
      };
    }]);
  */
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
}(this.window));