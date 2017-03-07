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
  var directives = angular.module('directives', []);
  directives.directive('thread', function ($compile) {
    // this is directive for showing message inside discussion
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
        " <li class=\"message\">" +
        "  <div class=\"media\">" +
        "   <div class=\"media-left\">" +
        "    <img class=\"user-avatar-chat\" data-ng-src=\"{{thread.message.user.avatarUrl}}\" " +
        "     alt=\"userpic\"/>" +
        "   </div>" +
        "   <div class=\"media-body\">" +
        "    <h5>" +
        "     <a href = \"javascript:void(0)\" data-ng-click=\"enterPrivateChannel(thread.message.user.username,thread.message.user.nickname)\">" +
        "           {{thread.message.user.nickname}}" +
        "     </a>" +
        "    </h5>" +
        "    <h6>posted at: {{thread.message.messageTime | date: \"HH:mm:ss dd/MM/yy\" }}</h6>" +
        "    <p>{{thread.message.content}}</p>" +
        "    <table class=\"chat-reply\" data-ng-show=\"isReply\">" +
        "     <tr>" +
        "      <td>" +
        "       <input type=\"text\" maxlength=\"500\" class=\"form-control chat-reply\" placeholder=\"Type your message here \"" +
        "        data-ng-model=\"lastReply\" data-ng-focus=\"setReply(thread.message.id)\" " +
        "        data-ng-enter=\"sendMessage(lastReply); isReply = false; lastReply = ''\" />" +
        "      </td>" +
        "      <td>" +
        "       <span class=\"input-group-btn chat-reply\">" +
        "        <button class=\"btn btn-primary btn-chat-send chat-reply\" " +
        "         type=\"submit\" data-ng-click=\"sendMessage(lastReply); isReply = false; lastReply = ''\">Send</button>" +
        "       </span>" +
        "      </td>" +
        "     </tr>" +
        "    </table>" +
        "    <span class=\"block-left\" data-ng-hide=\"isReply\">" +
        "     <a href=\"javascript: void(0)\" data-ng-click=\"isReply = true\">Reply</a>" + // tODO normally
        "    </span>" +
        "   </div>" +
        "  </div>" +
        " </li>" +
        "</ul>",
      link: function (scope, element, attrs) {
        //check if this member has children
        if (angular.isArray(scope.thread.replies)) {
          $compile('<discussion discussion="thread.replies" enter-private-channel="enterPrivateChannel"' +
            'set-reply="setReply" send-message="sendMessage"></discussion>')(scope, function (cloned, scope) {
            element.append(cloned);
          });
        }
      }
    };
  }).directive('discussion', function () {
    // this is directive to show tchannel thread recursively
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
    // this directive is used to download more messages if chat thread is fully scrolled down
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
    // this is directive to be able to hit enter
    // on search channels field and send messages fields
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
    // this directive is used to close search results 
    // and creation of new channel form if click outside happened
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