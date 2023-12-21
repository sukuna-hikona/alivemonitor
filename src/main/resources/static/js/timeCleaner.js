// JavaScript Document

$(function() {
    let maxDay = 31;

    for (let i = 0; i < maxDay; i++) {
        if (document.getElementById("scheduleDataList" + i + ".hour").value == "77") {
            document.getElementById("scheduleDataList" + i + ".hour").value = "";
        }
        if (document.getElementById("scheduleDataList" + i + ".minute").value == "77") {
            document.getElementById("scheduleDataList" + i + ".minute").value = "";
        }
    }
});