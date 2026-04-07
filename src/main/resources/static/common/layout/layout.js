(function () {
    var toggleBtn = document.getElementById('sidebarToggle');
    var appShell = document.getElementById('appShell');

    if (!toggleBtn || !appShell) {
        return;
    }

    toggleBtn.addEventListener('click', function () {
        appShell.classList.toggle('sidebar-collapsed');
    });
})();

