(function () {
    var enabledInput = document.getElementById('settingEnabled');
    var timeInput = document.getElementById('settingTime');
    var saveButton = document.getElementById('saveSettingBtn');
    var alertBox = document.querySelector('.js-setting-alert');

    if (!enabledInput || !timeInput || !saveButton || !alertBox) {
        return;
    }

    function showMessage(message, isError) {
        alertBox.classList.remove('d-none', 'alert-success', 'alert-danger');
        alertBox.classList.add(isError ? 'alert-danger' : 'alert-success');
        alertBox.textContent = message;
    }

    function parseTime(value) {
        var parts = (value || '').split(':');
        if (parts.length !== 2) {
            return null;
        }

        var hour = parseInt(parts[0], 10);
        var minute = parseInt(parts[1], 10);
        if (Number.isNaN(hour) || Number.isNaN(minute)) {
            return null;
        }

        return { hour: hour, minute: minute };
    }

    function formatTime(hour, minute) {
        var hh = String(hour).padStart(2, '0');
        var mm = String(minute).padStart(2, '0');
        return hh + ':' + mm;
    }

    function setLoading(isLoading) {
        saveButton.disabled = isLoading;
        saveButton.textContent = isLoading ? 'Saving...' : 'Save';
    }

    function loadConfig() {
        setLoading(true);
        fetch('/config/birthday', {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        })
            .then(function (response) {
                if (!response.ok) {
                    throw new Error('Khong the tai cau hinh hien tai');
                }
                return response.json();
            })
            .then(function (data) {
                enabledInput.checked = !!data.enabled;
                timeInput.value = formatTime(data.hour, data.minute);
            })
            .catch(function (error) {
                showMessage(error.message || 'Khong the tai cau hinh', true);
            })
            .finally(function () {
                setLoading(false);
            });
    }

    saveButton.addEventListener('click', function () {
        var time = parseTime(timeInput.value);
        if (!time) {
            showMessage('Vui long nhap thoi gian hop le', true);
            return;
        }

        setLoading(true);
        fetch('/config/birthday', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                enabled: enabledInput.checked,
                hour: time.hour,
                minute: time.minute
            })
        })
            .then(function (response) {
                if (!response.ok) {
                    throw new Error('Luu cau hinh that bai');
                }
                return response.json();
            })
            .then(function (data) {
                enabledInput.checked = !!data.enabled;
                timeInput.value = formatTime(data.hour, data.minute);
                showMessage('Luu cau hinh thanh cong', false);
            })
            .catch(function (error) {
                showMessage(error.message || 'Luu cau hinh that bai', true);
            })
            .finally(function () {
                setLoading(false);
            });
    });

    loadConfig();
})();

