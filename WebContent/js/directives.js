(function (global) {

  'use strict';
  /*global angular, console*/
  var directives = angular.module('directives', []);
  directives.directive('thread', function ($compile) {
    return {
      restrict: "E",
      replace: true,
      scope: {
        thread: '='
      },
      template: "<li><p>{{thread.Message.UserID}} on {{thread.Message.MessageTime}}: {{ thread.Message.Content}}</p></li>",
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