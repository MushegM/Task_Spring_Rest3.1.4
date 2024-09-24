
    $(document).ready(function() {
    loadUser();
    loadRoles();

    $('#createUserForm').on('submit', function(event) {
    event.preventDefault();
    createUser();
});

    $('#editUserForm').on('submit', function(event) {
    event.preventDefault();
    updateUser();
});
});
    function loadUser() {
    fetch('/api/user')
        .then(response => response.json())
        .then(user => {
            const tableBody = $('#userTable tbody');
            tableBody.empty();
            const row = `
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.username}</td>
                            <td>${user.lastName}</td>
                            <td>${user.email}</td>
                            <td>${user.age}</td>
                            <td>${user.roles.map(role => role.role).join(', ')}</td>
                        </tr>
                    `;

            tableBody.append(row);

        });
}

    function loadRoles() {
    fetch('/api/roles')
        .then(response => response.json())
        .then(data => {
            const rolesSelect = $('#roles');
            const editRolesSelect = $('#editRoles');
            rolesSelect.empty();
            editRolesSelect.empty();
            data.forEach(role => {
                rolesSelect.append(`<option value="${role.id}">${role.role}</option>`);
                editRolesSelect.append(`<option value="${role.id}">${role.role}</option>`);
            });
        });
}
