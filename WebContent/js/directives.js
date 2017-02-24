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
        enterPrivateChannel: '=',
        setReply: '=',
        sendMessage: '='
      },
      template: "" +
        "<ul class = \"messages\">" +
        "<li class=\"message\">" +
        " <div class=\"media\">" +
        "<div class=\"media-left\">" +
        "<img class=\"user-avatar-chat\" data-ng-src=\"{{thread.Message.User.AvatarUrl}}\" alt=\"userpic\"/>" +
        "</div>" +
        "<div class=\"media-body\">" +
        "<h5><a href = \"javascript:void(0)\" data-ng-click=\"enterPrivateChannel(thread.Message.User.Username,thread.Message.User.Nickname)\">{{thread.Message.User.Nickname}}</a></h5>" +
        "<h6>posted at: {{thread.Message.MessageTime | date: \"HH:mm:ss dd/MM/yy\" }}</h6>" +
        "<p>{{thread.Message.Content}}</p>" +
        "<table class=\"chat-reply\" data-ng-show=\"isReply\">" +
        " <tr>" +
        "<td>" +
        "<input type=\"text\" class=\"form-control chat-reply\" placeholder=\"Type your message here \" data-ng-model=\"lastReply\" data-ng-focus=\"setReply(thread.Message.Id)\" data-ng-enter=\"sendMessage(lastReply); isReply = false; lastReply = ''\" />" +
        "</td>" +
        "<td>" +
        "<span class=\"input-group-btn chat-reply\">" +
        "<button class=\"btn btn-primary btn-chat-send chat-reply\" type=\"submit\" data-ng-click=\"sendMessage(lastReply); isReply = false; lastReply = ''\">Send</button>" +
        "</span>" +
        "</td>" +
        " </tr>" +
        "</table>" +
        "<span class=\"block-right\" data-ng-hide=\"isReply\">" +
        "<a href=\"javascript: void(0)\" data-ng-click=\"isReply = true\">Reply</a>" + // TODO normally
        "</span>" +
        "</div>" +
        "</div>" +
        "</li>" +
        "</ul>",
      link: function (scope, element, attrs) {
        //check if this member has children
        if (angular.isArray(scope.thread.Replies)) {
          $compile('<discussion discussion="thread.Replies" enter-private-channel="enterPrivateChannel" set-reply="setReply" send-message="sendMessage"></discussion>')(scope, function (cloned, scope) {
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
        enterPrivateChannel: '=',
        setReply: '=',
        sendMessage: '='
      },
      template: "" +
        "<ul class='messages'>" +
        " <thread data-ng-repeat='thread in discussion'" +
        "   thread='thread' enter-private-channel='enterPrivateChannel' " +
        "   set-reply='setReply' send-message='sendMessage'>" +
        "  </thread>" +
        "</ul>"
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
          if (el.scrollTop + element[0].offsetHeight >= element[0].scrollHeight) {
            // fully scrolled to bottom of element (the div)
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
  }).directive('clickOutside', function ($document) {

    return {
      restrict: 'A',
      scope: {
        clickOutside: '&'
      },

      link: function (scope, el, attr) {
        $document.on('click', function (e) {
          if (el !== e.target && !el[0].contains(e.target)) {
            scope.$apply(function () {
              scope.$eval(scope.clickOutside);
            });
          }
        });
      }
    };
  });
}(this.window));