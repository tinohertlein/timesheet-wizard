// ==UserScript==
// @name         clockify-expected-hours
// @namespace    dev.hertlein.timesheetwizard
// @version      1.0.0
// @description  Displays the amount of expected hours on Clockify reports page
// @author       Tino Hertlein
// @require      https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js
// @match        https://app.clockify.me/reports/*
// ==/UserScript==

const $ = window.$;

(function () {
    'use strict';

    const waitFor = (...selectors) => new Promise(resolve => {
        const delay = 2000
        const f = () => {
            const elements = selectors.map(selector => document.querySelector(selector))
            if (elements.every(element => element != null)) {
                resolve(elements)
            } else {
                setTimeout(f, delay)
            }
        }
        f()
    })

    waitFor('summary-group-header', 'app-doughnut-chart')
        .then(([_]) => {
            displayExpectedHours()
        })
})();

function displayExpectedHours() {
    const expectedHours = calculateExpectedHours()
    const expectedHoursNode = `<div class="cl-d-flex cl-align-items-end cl-ml-sm-2 cl-mt-2 cl-mt-sm-0 ng-star-inserted"><div class="cl-h6 cl-mb-0 cl-lh-1">Expected: </div><div class="cl-h2 cl-mb-0 cl-ml-1 cl-lh-1">${expectedHours}:00:00</div></div>`

    $("div[data-cy='BILLABLE-AMOUNT']").parent().after(elementOf(expectedHoursNode))
}

function calculateExpectedHours() {
    const daysWorked = [...document.querySelectorAll('.cl-reports-summary-table-col-2-1')].slice(1).length
    return daysWorked * 8
}

function elementOf(html) {
    const template = document.createElement('template');
    template.innerHTML = html.trim();
    return template.content.firstChild;
}