(function (global) {

  'use strict';
  /*global angular, console*/
  var filters = angular.module('filters', []);
  filters.filter("toDateObject", function () {
    return function (input) {
      return new Date(input);
    };
  });
}(this.window));