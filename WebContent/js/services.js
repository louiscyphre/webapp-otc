(function (global) {

  'use strict';
  /*global angular, console*/
  var services = angular.module('services', ['constants']);

  services.factory('MessageBus', ['$rootScope', function ($rootScope) {

    return {
      send: function (msg, data) {
        $rootScope.$broadcast(msg, data);
      }
    };
  }]).factory('Socket', ['$window', 'MessageBus', function ($window, MessageBus) {

    var websocket = {};
    return {
      connect: function connect() {
        if (websocket) {
          return;
        }
        var wsUri = "ws://" + $window.location.host + "/webChat/";
        websocket = new $window.WebSocket(wsUri);

        websocket.onopen = function (evt) {};
        websocket.onmessage = function (evt) {
          // Send event using first property name of jSon object, and attach
          // data, so listener can catch it
          MessageBus.send("'" + Object.keys(evt.data) + "'", JSON.parse(evt.data));
        };
        websocket.onerror = function (evt) {};

        websocket.onclose = function (evt) {
          websocket = null;
        };
      },

      send: function send(jSon) {
        MessageBus.send('unsubscribedChannel');
        if (websocket !== null) {
          websocket.send(jSon);
        }
        jSon = {};
      },

      logout: function logout() {
        websocket.close();
      }
    };
        }]).factory('highlightText', ['$sce', function ($sce) {

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

  }]);
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