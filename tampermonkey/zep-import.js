// ==UserScript==
// @name                    ZEP import
// @namespace               dev.hertlein.timesheetwizard
// @version                 2026-06-21
// @description             Imports timesheet entries from a given JSON file into ZEP
// @author                  Tino Hertlein
// @match                   https://www.zep-online.de/*
// @icon                    https://www.google.com/s2/favicons?sz=64&domain=zep-online.de
// @require                 https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js
// @run-at                  context-menu
// @grant                   GM_registerMenuCommand
// @grant                   GM_getResourceText
// @resource timesheet      [insert path/url to JSON file here]
// ==/UserScript==

const $ = window.$;

async function wait() {
    return new Promise(function (resolve) {
        setTimeout(resolve, 2000);
    });
}

async function importEntries() {
    const timesheet = await GM_getResourceText("timesheet")

    console.log("Started importing timesheet entries:");
    console.log(timesheet);

    for (const entry of JSON.parse(timesheet)) {
        $('#calendar-btn-create').trigger("click");

        await wait();

        $('input[name="datum"]').prop("value", entry.datum);
        $('#von').prop("value", entry.von);
        $('#bis').prop("value", entry.bis);
        if (entry.ort !== '') {
            $('#ort').prop("value", entry.ort);
        }
        $('#Speichern').trigger("click")

        await wait();
    }
    console.log("Finished importing timesheet entries:");
}

(function () {
    'use strict';
    GM_registerMenuCommand("Import timesheet entries", importEntries(), "");
})();