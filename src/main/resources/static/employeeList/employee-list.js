(function () {
    var modalElement = document.getElementById('employeeModal');
    var stateElement = document.getElementById('employeeModalState');
    var formElement = document.getElementById('employeeForm');
    var modalTitle = document.getElementById('employeeModalTitle');
    var submitButton = document.getElementById('employeeSubmitBtn');
    var createButton = document.getElementById('openCreateEmployeeModal');

    if (!modalElement || !stateElement || !formElement) {
        return;
    }

    function ensureBackdrop() {
        var existingBackdrop = document.querySelector('.modal-backdrop');
        if (existingBackdrop) {
            return existingBackdrop;
        }
        var backdrop = document.createElement('div');
        backdrop.className = 'modal-backdrop fade show';
        document.body.appendChild(backdrop);
        return backdrop;
    }

    function removeBackdrop() {
        var backdrop = document.querySelector('.modal-backdrop');
        if (backdrop) {
            backdrop.parentNode.removeChild(backdrop);
        }
    }

    function showModal() {
        modalElement.style.display = 'block';
        modalElement.classList.add('show');
        modalElement.setAttribute('aria-modal', 'true');
        modalElement.removeAttribute('aria-hidden');
        document.body.classList.add('modal-open');
        ensureBackdrop();
    }

    function hideModal() {
        modalElement.style.display = 'none';
        modalElement.classList.remove('show');
        modalElement.setAttribute('aria-hidden', 'true');
        modalElement.removeAttribute('aria-modal');
        document.body.classList.remove('modal-open');
        removeBackdrop();
    }

    function setMode(mode, action) {
        var isUpdate = mode === 'update';
        formElement.setAttribute('action', action || '/employees');
        if (modalTitle) {
            modalTitle.textContent = isUpdate ? 'Cập Nhật Nhân Viên' : 'Thêm Nhân Viên';
        }
        if (submitButton) {
            submitButton.textContent = isUpdate ? 'Cập Nhật' : 'Thêm Mới';
        }
    }

    function resetForm() {
        formElement.reset();
        var departmentInput = document.getElementById('departmentId');
        if (departmentInput) {
            departmentInput.value = '';
        }
    }

    if (createButton) {
        createButton.addEventListener('click', function () {
            setMode('create', '/employees');
            resetForm();
            showModal();
        });
    }

    var editButtons = document.querySelectorAll('.js-edit-employee');
    editButtons.forEach(function (button) {
        button.addEventListener('click', function (event) {
            event.preventDefault();

            var employeeId = button.getAttribute('data-employee-id');
            setMode('update', '/employees/' + employeeId);

            document.getElementById('departmentId').value = button.getAttribute('data-department-id') || '';
            document.getElementById('jobTitle').value = button.getAttribute('data-job-title') || '';
            document.getElementById('employeeCode').value = button.getAttribute('data-employee-code') || '';
            document.getElementById('fullName').value = button.getAttribute('data-full-name') || '';
            document.getElementById('dateOfBirth').value = button.getAttribute('data-date-of-birth') || '';
            document.getElementById('phoneNumber').value = button.getAttribute('data-phone-number') || '';
            document.getElementById('email').value = button.getAttribute('data-email') || '';

            showModal();
        });
    });

    var dismissButtons = modalElement.querySelectorAll('[data-dismiss="modal"]');
    dismissButtons.forEach(function (button) {
        button.addEventListener('click', function () {
            hideModal();
        });
    });

    modalElement.addEventListener('click', function (event) {
        if (event.target === modalElement) {
            hideModal();
        }
    });

    var shouldOpenModal = stateElement.getAttribute('data-open-modal') === 'true';
    if (shouldOpenModal) {
        setMode(
            stateElement.getAttribute('data-modal-mode') || 'create',
            stateElement.getAttribute('data-form-action') || '/employees'
        );
        showModal();
    }
})();


