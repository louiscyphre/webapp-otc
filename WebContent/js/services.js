(function (global) {

  'use strict';
  /*global angular, console*/
  var services = angular.module('services', []);

  services.factory('MessageBus', ['$rootScope', function ($rootScope) {

    return {
      send: function (msg, data) {
        $rootScope.$broadcast(msg, data);
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
}(this.window));