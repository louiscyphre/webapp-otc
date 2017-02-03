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
      template: "<li><table><tr><td><div class=\"message-info\"> <img class=\"user-avatar-chat\" data-ng-src=\"{{thread.Message.User.AvatarUrl}}\" alt=\"userpic\" /> <button class=\"channel-control chat-description\"><img class=\"channel-control chat-description channel-control-icon\" data-ng-src=\"css/img/glyphicons-info-sign.png\" alt=\"description\" src=\"data:image/gif;base64,R0lGODlhAQABAAD/ACwAAAAAAQABAAACADs=\"/><div class=\"chat-description-content\"> {{thread.Message.User.Description}} </div></button> <a href=\"javascript:void(0)\" data-ng-click=\"enterPrivateChannel(user.Username)\">{{thread.Message.User.Nickname}}</a> </div></td><td><div class=\"message\"> {{thread.Message.MessageTime}}: {{ thread.Message.Content}}</div></td></tr></table></li>",
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
      template: "<ul><thread data-ng-repeat='thread in discussion' thread='thread'></thread></ul>"
    };
  });

}(this.window));