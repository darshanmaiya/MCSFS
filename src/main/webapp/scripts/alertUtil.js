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