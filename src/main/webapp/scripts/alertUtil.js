/*
    Copyright (C) 2016 DropTheBox (Aviral Takkar, Darshan Maiya, Wei-Tsung Lin)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
(function () {
	var alertUtil = {};
	
	alertUtil.hideAll = function () {
		$("#success-alert").addClass("hide");
		$("#warning-alert").addClass("hide");
		$("#danger-alert").addClass("hide");
	}
	
	alertUtil.success = function (string) {
		alertUtil.hideAll();
		$("#success-alert").text(string).removeClass("hide");
	}
	
	alertUtil.warning = function (string) {
		alertUtil.hideAll();
		$("#warning-alert").text(string).removeClass("hide");
	}
	
	alertUtil.danger = function (string) {
		alertUtil.hideAll();
		$("#danger-alert").text(string).removeClass("hide");
	}
	
	window.alertUtil = alertUtil;
})();