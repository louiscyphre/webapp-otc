(function (global) {

  'use strict';
  /*global angular, console*/
  var constants = angular.module('constants', []);
  constants.constant('channel_icon', 'img/glyphicons-group.png')
    .constant('envelope-icon', 'img/glyphicons-envelope.png')
    .constant('bell-icon', 'img/glyphicons-bell.png');

}(this.window));