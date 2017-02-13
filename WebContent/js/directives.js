(function (global) {

  'use strict';
  /*global angular, console*/
  var directives = angular.module('directives', ['constants', 'services']);
  directives.directive('thread', function ($compile) {
    return {
      restrict: "E",
      replace: true,
      scope: {
        thread: '=',
        enterPrivateChannel1: '&'
      },
      template: "<li>" +
        " <table>" +
        "    <tr>" +
        "      <td>" +
        "        <img class='user-avatar-chat' data-ng-src='{{thread.Message.User.AvatarUrl}}' alt='userpic' />" +
        "      </td>" +
        "      <td>" +
        "        <div class='message'> <a href='javascript:void(0)' " +
        "          data-ng-click='enterPrivateChannel1(thread.Message.User.Username,thread.Message.User.Nickname)'>" +
        "          <button class='channel-control chat-description'><img class='channel-control chat-description channel-control-icon' " +
        "             data-ng-src='css/img/glyphicons-info-sign.png' alt='description'" +
        "              src='data:image/gif;base64,R0lGODlhAQABAAD/ACwAAAAAAQABAAACADs='/>" +
        "            <div class=chat-description-content> {{thread.Message.User.Description}} </div>" +
        "          </button>" +
        "            {{thread.Message.User.Nickname}}</a> on {{thread.Message.MessageTime}}: {{ thread.Message.Content}}" +
        "        </div>" +
        "      </td>" +
        "    </tr>" +
        "  </table>" +
        "</li>",
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
        discussion: '=',
        enterPrivateChannel1: '&'
      },
      template: "<ul><thread data-ng-repeat='thread in discussion' thread='thread'  enter-private-channel1='enterPrivateChannel(thread.Message.User.Username, thread.Message.User.Nickname)'></thread></ul>"
    };
  }).directive('scrolledDownCallback', function () {
    return {
      restrict: 'A',
      scope: {
        scrolledDownCallback: '='
      },
      link: function (scope, element, attrs) {
        var clientHeight = element[0].clientHeight;

        element.bind('scroll', function (e) {
          var el = e.target;

          if ((el.scrollHeight - el.scrollTop) === clientHeight) {
            element.scope().downloadOnScroll();
          }
        });
      }
    };
  }).directive('ngEnter', function () {
    return function (scope, element, attrs) {
      element.bind("keydown keypress", function (event) {
        if (event.which === 13) {
          scope.$apply(function () {
            scope.$eval(attrs.ngEnter, {
              'event': event
            });
          });
          event.preventDefault();
        }
      });
    };
  });

}(this.window));