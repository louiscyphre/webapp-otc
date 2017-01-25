(function (global) {

  'use strict';
  /*global angular, console*/
  var directives = angular.module('directives', ['constants']);
  directives.directive('thread', function ($compile) {
    return {
      restrict: "E",
      replace: true,
      scope: {
        thread: '='
      },
      template: "<li><table><tr><td><div class=\"message-info\">{{thread.Message.UserID}} {{thread.Message.MessageTime}} </div></td><td><div class=\"message\">{{ thread.Message.Content}}</div></td></tr></table></li>",
      link: function (scope, element, attrs) {
        //check if this member has children
        if (angular.isArray(scope.thread.Replies)) {
          $compile('<discussion discussion="thread.Replies"></discussion>')(scope, function (cloned, scope) {
            element.append(cloned);
          });
        }
      }
    };
  }).directive('discussion', function () {
    return {
      restrict: "E",
      replace: true,
      scope: {
        discussion: '='
      },
      template: "<ul><thread ng-repeat='thread in discussion' thread='thread'></thread></ul>"
    };
  });

}(this.window));