/**
 * Reemplaza onclick inline (mejor para Content-Security-Policy script-src 'self').
 */
document.addEventListener('DOMContentLoaded', function () {
	document.querySelectorAll('.js-print').forEach(function (btn) {
		btn.addEventListener('click', function () {
			window.print();
		});
	});
});
