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
      template: "<li><table><tr><td></td><td><div class=\"message\"><div class=\"message-info\"> <img class=\"user-avatar-chat\" data-ng-src=\"{{thread.Message.User.AvatarURL}}\" alt=\"userpic\" /> <button class=\"channel-control description\"><img class=\"channel-control description channel-control-icon\" data-ng-src=\"img/glyphicons-info-sign.png\" alt=\"description\" /><div class=\"description-content\"> {{thread.Message.User.Description}} </div></button> {{thread.Message.User.Nickname}} {{thread.Message.MessageTime}} </div>{{ thread.Message.Content}}</div></td></tr></table></li>",
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