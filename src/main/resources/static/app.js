// Global variables
let authToken = null;
let currentUser = null;
const API_BASE = 'http://localhost:8082/api';

// Initialize the app
document.addEventListener('DOMContentLoaded', function() {
    // Check if user is already logged in
    const savedToken = localStorage.getItem('authToken');
    const savedUser = localStorage.getItem('currentUser');
    
    if (savedToken && savedUser) {
        authToken = savedToken;
        currentUser = JSON.parse(savedUser);
        showMainContent();
    }

    // Set up form event listeners
    setupEventListeners();
});

function setupEventListeners() {
    // Login form
    document.getElementById('loginForm').addEventListener('submit', handleLogin);
    
    // Register form
    document.getElementById('registerForm').addEventListener('submit', handleRegister);
    
    // Tab change events
    document.getElementById('mainTabs').addEventListener('shown.bs.tab', function (event) {
        const target = event.target.getAttribute('data-bs-target');
        switch(target) {
            case '#bills':
                loadBills();
                break;
            case '#meters':
                loadMeters();
                break;
            case '#consumption':
                loadConsumption();
                break;
            case '#tariffs':
                loadTariffs();
                break;
        }
    });
}

// Authentication functions
async function handleLogin(event) {
    event.preventDefault();
    
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;
    
    try {
        showLoading('Đang đăng nhập...');
        
        const response = await fetch(`${API_BASE}/customers/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password })
        });
        
        if (response.ok) {
            const data = await response.json();
            authToken = data.token;
            currentUser = { email: data.email, fullName: data.fullName, id: data.id }; // Thêm id vào currentUser
            // Save to localStorage
            localStorage.setItem('authToken', authToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            showAlert('Đăng nhập thành công!', 'success');
            showMainContent();
        } else {
            const error = await response.text();
            showAlert(error || 'Đăng nhập thất bại!', 'danger');
        }
    } catch (error) {
        showAlert('Lỗi kết nối: ' + error.message, 'danger');
    } finally {
        hideLoading();
    }
}

async function handleRegister(event) {
    event.preventDefault();
    
    // Client-side validation
    const fullName = document.getElementById('regFullName').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const phone = document.getElementById('regPhone').value.trim();
    const password = document.getElementById('regPassword').value;
    const location = document.getElementById('regAddress').value.trim();
    
    // Validate inputs
    if (fullName.length < 2) {
        showAlert('Họ và tên phải có ít nhất 2 ký tự!', 'danger');
        return;
    }
    
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        showAlert('Định dạng email không hợp lệ!', 'danger');
        return;
    }
    
    if (!/^\d{10}$/.test(phone)) {
        showAlert('Số điện thoại phải có đúng 10 chữ số!', 'danger');
        return;
    }
    
    if (password.length < 6) {
        showAlert('Mật khẩu phải có ít nhất 6 ký tự!', 'danger');
        return;
    }
    
    if (!location) {
        showAlert('Vui lòng nhập địa chỉ!', 'danger');
        return;
    }
    
    const formData = {
        fullName: fullName,
        email: email,
        phone: phone,
        password: password,
        location: location
    };
    
    try {
        showLoading('Đang đăng ký...');
        
        const response = await fetch(`${API_BASE}/customers/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        });
        
        if (response.ok) {
            showAlert('Đăng ký thành công! Vui lòng đăng nhập.', 'success');
            // Switch to login tab
            document.getElementById('login-tab').click();
            document.getElementById('registerForm').reset();
        } else {
            const errorText = await response.text();
            let errorMessage = 'Đăng ký thất bại!';
            
            // Try to parse JSON error response
            try {
                const errorObj = JSON.parse(errorText);
                if (errorObj.message) {
                    errorMessage = errorObj.message;
                }
            } catch (e) {
                // If not JSON, use the text directly
                if (errorText.includes('Email already exists')) {
                    errorMessage = 'Email này đã được sử dụng!';
                } else if (errorText.includes('Phone number already exists')) {
                    errorMessage = 'Số điện thoại này đã được sử dụng!';
                } else if (errorText.includes('Location is required')) {
                    errorMessage = 'Vui lòng nhập địa chỉ!';
                } else if (errorText.includes('Phone number must be 10 digits')) {
                    errorMessage = 'Số điện thoại phải có đúng 10 chữ số!';
                } else if (errorText.includes('Password must be at least 6 characters')) {
                    errorMessage = 'Mật khẩu phải có ít nhất 6 ký tự!';
                } else if (errorText.includes('Invalid email format')) {
                    errorMessage = 'Định dạng email không hợp lệ!';
                } else if (errorText) {
                    errorMessage = errorText;
                }
            }
            
            showAlert(errorMessage, 'danger');
        }
    } catch (error) {
        showAlert('Lỗi kết nối: ' + error.message, 'danger');
    } finally {
        hideLoading();
    }
}

function logout() {
    authToken = null;
    currentUser = null;
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    
    document.getElementById('authSection').style.display = 'block';
    document.getElementById('mainContent').classList.remove('show');
    
    showAlert('Đã đăng xuất thành công!', 'info');
}

function showMainContent() {
    document.getElementById('authSection').style.display = 'none';
    document.getElementById('mainContent').classList.add('show');
    
    // Update user info
    document.getElementById('userInfo').textContent = `Xin chào, ${currentUser.fullName}`;
    
    // Load initial data
    loadBills();
    loadStatistics();
}

// API functions with authentication
async function apiCall(endpoint, options = {}) {
    const config = {
        headers: {
            'Content-Type': 'application/json',
            ...(authToken && { 'Authorization': `Bearer ${authToken}` })
        },
        ...options
    };
    
    try {
        const response = await fetch(`${API_BASE}${endpoint}`, config);
        
        if (response.status === 401) {
            logout();
            throw new Error('Phiên đăng nhập hết hạn');
        }
        
        return response;
    } catch (error) {
        throw error;
    }
}

// Data loading functions
async function loadBills() {
    try {
        const response = await apiCall('/bills');
        
        if (response.ok) {
            const bills = await response.json();
            displayBills(bills);
        } else {
            throw new Error('Không thể tải danh sách hóa đơn');
        }
    } catch (error) {
        document.getElementById('billsContent').innerHTML = `
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-triangle"></i> ${error.message}
            </div>
        `;
    }
}

async function loadMeters() {
    try {
        const response = await apiCall('/meters');
        
        if (response.ok) {
            const meters = await response.json();
            displayMeters(meters);
        } else {
            throw new Error('Không thể tải danh sách đồng hồ điện');
        }
    } catch (error) {
        document.getElementById('metersContent').innerHTML = `
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-triangle"></i> ${error.message}
            </div>
        `;
    }
}

async function loadConsumption() {
    try {
        console.log('[loadConsumption] Loading consumption data...');
        const response = await apiCall('/consumptions');
        
        console.log('[loadConsumption] Response status:', response.status);
        
        if (response.ok) {
            const consumption = await response.json();
            console.log('[loadConsumption] Loaded consumption data:', consumption);
            displayConsumption(consumption);
        } else {
            const errorText = await response.text();
            console.error('[loadConsumption] Server error:', errorText);
            throw new Error('Không thể tải dữ liệu tiêu thụ: ' + errorText);
        }
    } catch (error) {
        console.error('[loadConsumption] Exception:', error);
        document.getElementById('consumptionContent').innerHTML = `
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-triangle"></i> ${error.message}
            </div>
        `;
    }
}

async function loadTariffs() {
    try {
        // This endpoint is public, no auth needed
        const response = await fetch(`${API_BASE}/tariffs`);
        
        if (response.ok) {
            const tariffs = await response.json();
            displayTariffs(tariffs);
        } else {
            throw new Error('Không thể tải biểu giá điện');
        }
    } catch (error) {
        document.getElementById('tariffsContent').innerHTML = `
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-triangle"></i> ${error.message}
            </div>
        `;
    }
}

// Display functions
function displayBills(bills) {
    const content = document.getElementById('billsContent');
    
    if (bills.length === 0) {
        content.innerHTML = `
            <div class="alert alert-info fade-in">
                <div class="text-center py-4">
                    <i class="fas fa-file-invoice-dollar fa-3x text-muted mb-3"></i>
                    <h5>Chưa có hóa đơn nào</h5>
                    <p class="text-muted">Bắt đầu bằng cách tạo hóa đơn mới cho đồng hồ điện của bạn.</p>
                    <button class="btn btn-warning" data-bs-toggle="modal" data-bs-target="#generateBillModal" onclick="loadMetersForBill()">
                        <i class="fas fa-file-plus me-2"></i>Tạo hóa đơn đầu tiên
                    </button>
                </div>
            </div>
        `;
        return;
    }
    
    const table = `
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h5 class="mb-0">
                <i class="fas fa-file-invoice-dollar me-2"></i>
                Danh sách hóa đơn (${bills.length})
            </h5>
            <div class="btn-group">
                <div class="dropdown">
                    <button class="btn btn-success dropdown-toggle" type="button" data-bs-toggle="dropdown" title="Xuất Excel">
                        <i class="fas fa-file-excel me-2"></i>Xuất Excel
                    </button>
                    <ul class="dropdown-menu">
                        <li><h6 class="dropdown-header">
                            <i class="fas fa-download me-1"></i>Tùy chọn xuất
                        </h6></li>
                        <li><a class="dropdown-item" href="#" onclick="exportBillsToExcel()">
                            <i class="fas fa-file-excel me-2 text-success"></i>Xuất tất cả hóa đơn
                        </a></li>
                        <li><a class="dropdown-item" href="#" onclick="exportBillsToExcelWithFilter()">
                            <i class="fas fa-filter me-2 text-primary"></i>Xuất với bộ lọc nâng cao
                        </a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="#" onclick="exportCurrentMonthBills()">
                            <i class="fas fa-calendar-alt me-2 text-warning"></i>Xuất hóa đơn tháng này
                        </a></li>
                        <li><a class="dropdown-item" href="#" onclick="exportMyBills()">
                            <i class="fas fa-user me-2 text-info"></i>Xuất hóa đơn của tôi
                        </a></li>
                    </ul>
                </div>
                <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#generateBillModal" onclick="loadMetersForBill()" title="Tạo hóa đơn mới">
                    <i class="fas fa-plus me-2"></i>Tạo hóa đơn
                </button>
            </div>
        </div>
        <div class="table-container fade-in">
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th><i class="fas fa-hashtag me-2"></i>Mã HĐ</th>
                            <th><i class="fas fa-calendar me-2"></i>Kỳ hóa đơn</th>
                            <th><i class="fas fa-bolt me-2"></i>Tiêu thụ</th>
                            <th><i class="fas fa-money-bill me-2"></i>Tổng tiền</th>
                            <th><i class="fas fa-calendar-check me-2"></i>Hạn thanh toán</th>
                            <th><i class="fas fa-info-circle me-2"></i>Trạng thái</th>
                            <th><i class="fas fa-cogs me-2"></i>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${bills.map(bill => `
                            <tr>
                                <td>
                                    <div class="d-flex align-items-center">
                                        <div class="avatar-sm bg-primary rounded-circle me-3">
                                            <i class="fas fa-file-invoice text-white"></i>
                                        </div>
                                        <div>
                                            <strong>#${bill.id}</strong>
                                            <br><small class="text-muted">Đồng hồ: ${bill.meter?.meterNumber || 'N/A'}</small>
                                        </div>
                                    </div>
                                </td>
                                <td>
                                    <strong>${bill.billingPeriod || 'N/A'}</strong>
                                    <br><small class="text-muted">Tạo: ${formatDate(bill.createdAt)}</small>
                                </td>
                                <td>
                                    <span class="badge bg-info">${bill.unitsConsumed || 0} kWh</span>
                                    <br><small class="text-muted">Cũ: ${bill.previousReading || 0} → Mới: ${bill.currentReading || 0}</small>
                                </td>
                                <td>
                                    <strong class="text-success">${formatCurrency(bill.totalAmount || 0)}</strong>
                                    <br><small class="text-muted">${formatCurrency(bill.rate || 0)}/kWh</small>
                                </td>
                                <td>
                                    <strong>${formatDate(bill.dueDate)}</strong>
                                    ${new Date(bill.dueDate) < new Date() && bill.status !== 'PAID' ? 
                                        '<br><small class="text-danger"><i class="fas fa-exclamation-triangle"></i> Quá hạn</small>' : ''}
                                </td>
                                <td>
                                    <span class="badge ${bill.status === 'PAID' ? 'bg-success' : bill.status === 'OVERDUE' ? 'bg-danger' : 'bg-warning'}">
                                        <i class="fas ${bill.status === 'PAID' ? 'fa-check' : bill.status === 'OVERDUE' ? 'fa-exclamation-triangle' : 'fa-clock'}"></i>
                                        ${bill.status === 'PAID' ? 'Đã thanh toán' : bill.status === 'OVERDUE' ? 'Quá hạn' : 'Chưa thanh toán'}
                                    </span>
                                </td>
                                <td>
                                    <div class="btn-group" role="group">
                                        <button class="btn btn-sm btn-outline-primary" onclick="viewBillDetail(${bill.id})" title="Xem chi tiết">
                                            <i class="fas fa-eye"></i>
                                        </button>
                                        ${bill.status !== 'PAID' ? 
                                            `<button class="btn btn-sm btn-outline-success" onclick="payBill(${bill.id})" title="Thanh toán">
                                                <i class="fas fa-credit-card"></i>
                                            </button>` : ''
                                        }
                                    </div>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        </div>
    `;
    
    content.innerHTML = table;
}

function displayMeters(meters) {
    const content = document.getElementById('metersContent');
    
    if (meters.length === 0) {
        content.innerHTML = `
            <div class="alert alert-info fade-in">
                <div class="text-center py-4">
                    <i class="fas fa-tachometer-alt fa-3x text-muted mb-3"></i>
                    <h5>Chưa có đồng hồ điện nào</h5>
                    <p class="text-muted">Thêm đồng hồ điện đầu tiên để bắt đầu quản lý tiêu thụ điện.</p>
                    <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addMeterModal">
                        <i class="fas fa-plus me-2"></i>Thêm đồng hồ đầu tiên
                    </button>
                </div>
            </div>
        `;
        return;
    }
    
    const table = `
        <div class="table-container fade-in">
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th><i class="fas fa-hashtag me-2"></i>Số đồng hồ</th>
                            <th><i class="fas fa-cog me-2"></i>Loại</th>
                            <th><i class="fas fa-calendar me-2"></i>Ngày lắp đặt</th>
                            <th><i class="fas fa-tachometer-alt me-2"></i>Chỉ số hiện tại</th>
                            <th><i class="fas fa-signal me-2"></i>Trạng thái</th>
                            <th><i class="fas fa-cogs me-2"></i>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${meters.map(meter => `
                            <tr>
                                <td>
                                    <div class="d-flex align-items-center">
                                        <div class="avatar-sm bg-success rounded-circle me-3">
                                            <i class="fas fa-tachometer-alt text-white"></i>
                                        </div>
                                        <div>
                                            <strong>${meter.meterNumber}</strong>
                                            <br><small class="text-muted">ID: #${meter.id}</small>
                                        </div>
                                    </div>
                                </td>
                                <td>
                                    <span class="badge ${meter.meterType === 'SINGLE_PHASE' ? 'bg-info' : 'bg-primary'}">
                                        <i class="fas ${meter.meterType === 'SINGLE_PHASE' ? 'fa-circle' : 'fa-layer-group'}"></i>
                                        ${meter.meterType === 'SINGLE_PHASE' ? 'Một pha' : 'Ba pha'}
                                    </span>
                                </td>
                                <td>
                                    <strong>${formatDate(meter.installationDate)}</strong>
                                    <br><small class="text-muted">Chỉ số ban đầu: ${meter.initialReading || 0} kWh</small>
                                </td>
                                <td>
                                    <span class="badge bg-warning text-dark">${meter.currentReading || meter.initialReading || 0} kWh</span>
                                </td>
                                <td>
                                    <span class="badge bg-success">
                                        <i class="fas fa-check"></i> Hoạt động
                                    </span>
                                </td>
                                <td>
                                    <div class="btn-group" role="group">
                                        <button class="btn btn-sm btn-outline-info" data-bs-toggle="modal" data-bs-target="#addConsumptionModal" onclick="loadMetersForConsumption(); document.getElementById('consumptionMeterId').value='${meter.id}'" title="Ghi chỉ số">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn btn-sm btn-outline-danger" onclick="deleteMeter(${meter.id})" title="Xóa đồng hồ">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        </div>
    `;
    
    content.innerHTML = table;
}

function displayConsumption(consumptions) {
    const content = document.getElementById('consumptionContent');
    
    if (consumptions.length === 0) {
        content.innerHTML = `
            <div class="alert alert-info fade-in">
                <div class="text-center py-4">
                    <i class="fas fa-chart-line fa-3x text-muted mb-3"></i>
                    <h5>Chưa có dữ liệu tiêu thụ</h5>
                    <p class="text-muted">Bắt đầu ghi chỉ số đồng hồ để theo dõi tiêu thụ điện.</p>
                    <button class="btn btn-info" data-bs-toggle="modal" data-bs-target="#addConsumptionModal" onclick="loadMetersForConsumption()">
                        <i class="fas fa-plus me-2"></i>Ghi chỉ số đầu tiên
                    </button>
                </div>
            </div>
        `;
        return;
    }
    
    const table = `
        <div class="table-container fade-in">
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th><i class="fas fa-tachometer-alt me-2"></i>Đồng hồ</th>
                            <th><i class="fas fa-calendar me-2"></i>Ngày ghi</th>
                            <th><i class="fas fa-arrow-up me-2"></i>Chỉ số cũ</th>
                            <th><i class="fas fa-arrow-down me-2"></i>Chỉ số mới</th>
                            <th><i class="fas fa-bolt me-2"></i>Tiêu thụ</th>
                            <th><i class="fas fa-sticky-note me-2"></i>Ghi chú</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${consumptions.map(consumption => `
                            <tr>
                                <td>
                                    <div class="d-flex align-items-center">
                                        <div class="avatar-sm bg-info rounded-circle me-3">
                                            <i class="fas fa-tachometer-alt text-white"></i>
                                        </div>
                                        <div>
                                            <strong>${consumption.meter?.meterNumber || 'N/A'}</strong>
                                            <br><small class="text-muted">${consumption.meter?.meterType || 'N/A'}</small>
                                        </div>
                                    </div>
                                </td>
                                <td>
                                    <strong>${formatDate(consumption.recordedDate)}</strong>
                                    <br><small class="text-muted">${formatDateTime(consumption.createdAt)}</small>
                                </td>
                                <td>
                                    <span class="badge bg-secondary">${consumption.previousReading || 0} kWh</span>
                                </td>
                                <td>
                                    <span class="badge bg-primary">${consumption.currentReading || 0} kWh</span>
                                </td>
                                <td>
                                    <span class="badge bg-success">${consumption.unitsConsumed || 0} kWh</span>
                                </td>
                                <td>
                                    <small class="text-muted">${consumption.notes || 'Không có'}</small>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        </div>
    `;
    
    content.innerHTML = table;
}

function displayTariffs(tariffs) {
    const content = document.getElementById('tariffsContent');
    
    if (tariffs.length === 0) {
        content.innerHTML = `
            <div class="alert alert-info fade-in text-center py-4">
                <i class="fas fa-info-circle fa-3x text-muted mb-3"></i>
                <h5>Chưa có biểu giá nào</h5>
                <p class="text-muted">Hệ thống chưa có biểu giá điện. Vui lòng liên hệ quản trị viên.</p>
                <button class="btn btn-primary" onclick="initSampleTariffs()">
                    <i class="fas fa-plus me-2"></i>Tạo biểu giá mẫu
                </button>
            </div>
        `;
        return;
    }
    
    const table = `
        <div class="table-container fade-in">
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th><i class="fas fa-hashtag me-2"></i>ID</th>
                            <th><i class="fas fa-money-bill-wave me-2"></i>Đơn giá</th>
                            <th><i class="fas fa-calendar-alt me-2"></i>Từ ngày</th>
                            <th><i class="fas fa-calendar-times me-2"></i>Đến ngày</th>
                            <th><i class="fas fa-info-circle me-2"></i>Trạng thái</th>
                            <th><i class="fas fa-cogs me-2"></i>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${tariffs.map(tariff => {
                            const now = new Date();
                            const validFrom = new Date(tariff.validFrom);
                            const validTo = new Date(tariff.validTo);
                            const isActive = now >= validFrom && now <= validTo;
                            const isFuture = now < validFrom;
                            const isExpired = now > validTo;
                            
                            let statusBadge = '';
                            if (isActive) {
                                statusBadge = '<span class="badge bg-success"><i class="fas fa-check me-1"></i>Đang áp dụng</span>';
                            } else if (isFuture) {
                                statusBadge = '<span class="badge bg-warning"><i class="fas fa-clock me-1"></i>Sắp áp dụng</span>';
                            } else {
                                statusBadge = '<span class="badge bg-secondary"><i class="fas fa-times me-1"></i>Đã hết hạn</span>';
                            }
                            
                            return `
                                <tr class="table-row-hover">
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <div class="avatar-sm bg-primary rounded-circle me-3">
                                                <i class="fas fa-bolt text-white"></i>
                                            </div>
                                            <strong>#${tariff.id}</strong>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <span class="badge bg-success fs-6">
                                                ${formatCurrency(tariff.ratePerUnit)}/kWh
                                            </span>
                                        </div>
                                    </td>
                                    <td>
                                        <i class="fas fa-calendar text-primary me-1"></i>
                                        <strong>${formatDate(tariff.validFrom)}</strong>
                                    </td>
                                    <td>
                                        <i class="fas fa-calendar text-danger me-1"></i>
                                        <strong>${formatDate(tariff.validTo)}</strong>
                                    </td>
                                    <td>
                                        ${statusBadge}
                                    </td>
                                    <td>
                                        <div class="btn-group" role="group">
                                            ${isActive ? `
                                                <button class="btn btn-sm btn-outline-success" onclick="useTariffForCalculation(${tariff.id})" title="Sử dụng tính toán">
                                                    <i class="fas fa-calculator"></i>
                                                </button>
                                            ` : ''}
                                            <button class="btn btn-sm btn-outline-info" onclick="viewTariffDetail(${tariff.id})" title="Xem chi tiết">
                                                <i class="fas fa-eye"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            `;
                        }).join('')}
                    </tbody>
                </table>
            </div>
        </div>
    `;
    
    content.innerHTML = table;
}

// Utility functions
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    try {
        const date = new Date(dateString);
        if (isNaN(date.getTime())) {
            return 'Invalid Date';
        }
        return date.toLocaleDateString('vi-VN');
    } catch (error) {
        console.error('Error formatting date:', dateString, error);
        return 'Invalid Date';
    }
}

function showAlert(message, type = 'info') {
    const alertContainer = document.getElementById('alertContainer');
    const alertId = 'alert_' + Date.now();
    
    const alertHTML = `
        <div id="${alertId}" class="alert alert-${type} alert-dismissible fade show" role="alert">
            <i class="fas fa-${getAlertIcon(type)}"></i> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    alertContainer.insertAdjacentHTML('beforeend', alertHTML);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        const alert = document.getElementById(alertId);
        if (alert) {
            alert.remove();
        }
    }, 5000);
}

function getAlertIcon(type) {
    switch(type) {
        case 'success': return 'check-circle';
        case 'danger': return 'exclamation-triangle';
        case 'warning': return 'exclamation-triangle';
        case 'info': return 'info-circle';
        default: return 'info-circle';
    }
}

function showLoading(message = 'Đang tải...') {
    // You can implement a loading overlay here
    console.log(message);
}

function hideLoading() {
    // Hide loading overlay
    console.log('Loading hidden');
}

// Statistics update function
function updateStatistics(data, type) {
    switch(type) {
        case 'bills':
            document.getElementById('totalBills').textContent = data.length;
            const unpaidBills = data.filter(bill => bill.status === 'UNPAID' || bill.status === 'PENDING');
            const unpaidTotal = unpaidBills.reduce((sum, bill) => sum + (bill.totalAmount || 0), 0);
            document.getElementById('unpaidAmount').textContent = formatCurrency(unpaidTotal);
            break;
        case 'meters':
            document.getElementById('totalMeters').textContent = data.length;
            break;
        case 'consumption':
            const totalConsumption = data.reduce((sum, item) => sum + (item.unitsConsumed || 0), 0);
            document.getElementById('totalConsumption').textContent = formatNumber(totalConsumption);
            break;
    }
}

// Additional utility functions
function formatNumber(number) {
    if (number >= 1000000) {
        return (number / 1000000).toFixed(1) + 'M';
    } else if (number >= 1000) {
        return (number / 1000).toFixed(1) + 'K';
    }
    return number.toString();
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

function formatDateTime(dateString) {
    if (!dateString) return 'N/A';
    try {
        const date = new Date(dateString);
        if (isNaN(date.getTime())) {
            return 'Invalid Date';
        }
        return date.toLocaleString('vi-VN');
    } catch (error) {
        console.error('Error formatting datetime:', dateString, error);
        return 'Invalid Date';
    }
}

// Modal and form management functions
function clearForm(formId) {
    document.getElementById(formId).reset();
}

function setCurrentDate(inputId) {
    const today = new Date().toISOString().split('T')[0];
    document.getElementById(inputId).value = today;
}

// Meter management functions
async function addMeter() {
    try {
        const meterNumber = document.getElementById('meterNumber').value;
        const meterType = document.getElementById('meterType').value;
        const installationDate = document.getElementById('installationDate').value;
        const initialReading = document.getElementById('initialReading').value;

        console.log('Form values:', { meterNumber, meterType, installationDate, initialReading });

        if (!meterNumber || !meterType || !installationDate || !initialReading) {
            showAlert('Vui lòng điền đầy đủ thông tin!', 'warning');
            return;
        }

        if (!currentUser || !currentUser.id) {
            showAlert('Phiên đăng nhập hết hạn, vui lòng đăng nhập lại!', 'danger');
            return;
        }

        const meterData = {
            meterNumber,
            meterType,
            installationDate,
            initialReading: parseFloat(initialReading),
            customerId: currentUser.id
        };

        console.log('Sending meter data:', meterData);

        const response = await apiCall('/meters', {
            method: 'POST',
            body: JSON.stringify(meterData)
        });

        console.log('Response status:', response.status);

        if (response.ok) {
            showAlert('Thêm đồng hồ điện thành công!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('addMeterModal')).hide();
            clearForm('addMeterForm');
            loadMeters();
            loadStatistics();
        } else {
            const errorText = await response.text();
            console.error('Server error:', errorText);
            showAlert('Lỗi: ' + errorText, 'danger');
        }
    } catch (error) {
        console.error('Add meter error:', error);
        showAlert('Lỗi kết nối: ' + error.message, 'danger');
    }
}

async function deleteMeter(meterId) {
    if (!confirm('Bạn có chắc chắn muốn xóa đồng hồ này? Hành động này không thể hoàn tác!')) {
        return;
    }

    try {
        const response = await apiCall(`/meters/${meterId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showAlert('Xóa đồng hồ thành công!', 'success');
            loadMeters();
            loadStatistics();
        } else {
            const errorText = await response.text();
            showAlert('Lỗi: ' + errorText, 'danger');
        }
    } catch (error) {
        showAlert('Lỗi kết nối: ' + error.message, 'danger');
    }
}

// Consumption management functions
async function loadMetersForConsumption() {
    try {
        const response = await apiCall('/meters');
        const meters = await response.json();
        
        const select = document.getElementById('consumptionMeterId');
        select.innerHTML = '<option value="">Chọn đồng hồ điện</option>';
        
        meters.forEach(meter => {
            select.innerHTML += `<option value="${meter.id}">${meter.meterNumber} - ${meter.meterType}</option>`;
        });

        setCurrentDate('readingDate');
    } catch (error) {
        showAlert('Không thể tải danh sách đồng hồ!', 'danger');
    }
}

async function addConsumption() {
    try {
        const meterId = document.getElementById('consumptionMeterId').value;
        const currentReading = document.getElementById('currentReading').value;
        const readingDate = document.getElementById('readingDate').value;
        const notes = document.getElementById('notes').value;

        console.log('[addConsumption] Form values:', { meterId, currentReading, readingDate, notes });

        if (!meterId || !currentReading || !readingDate) {
            showAlert('Vui lòng điền đầy đủ thông tin bắt buộc!', 'warning');
            return;
        }

        const consumptionData = {
            meterId: parseInt(meterId),
            currentReading: parseFloat(currentReading),
            readingDate,
            notes: notes || null
        };

        console.log('[addConsumption] Sending consumption data:', consumptionData);

        const response = await apiCall('/consumptions', {
            method: 'POST',
            body: JSON.stringify(consumptionData)
        });

        console.log('[addConsumption] Response status:', response.status);

        if (response.ok) {
            showAlert('Ghi chỉ số thành công!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('addConsumptionModal')).hide();
            clearForm('addConsumptionForm');
            loadConsumption();
            loadStatistics();
        } else {
            const errorText = await response.text();
            console.error('[addConsumption] Server error:', errorText);
            showAlert('Lỗi: ' + errorText, 'danger');
        }
    } catch (error) {
        console.error('[addConsumption] Exception:', error);
        showAlert('Lỗi kết nối: ' + error.message, 'danger');
    }
}

// Bill management functions
async function loadMetersForBill() {
    try {
        const response = await apiCall('/meters');
        const meters = await response.json();
        
        const select = document.getElementById('billMeterId');
        select.innerHTML = '<option value="">Chọn đồng hồ điện</option>';
        
        meters.forEach(meter => {
            select.innerHTML += `<option value="${meter.id}">${meter.meterNumber} - ${meter.meterType}</option>`;
        });

        // Set default billing period to current month
        const now = new Date();
        const currentMonth = now.toISOString().slice(0, 7);
        document.getElementById('billingPeriod').value = currentMonth;

        // Set default due date to next month
        const nextMonth = new Date(now.getFullYear(), now.getMonth() + 1, 15);
        document.getElementById('dueDate').value = nextMonth.toISOString().split('T')[0];
    } catch (error) {
        showAlert('Không thể tải danh sách đồng hồ!', 'danger');
    }
}

async function generateBill() {
    try {
        const meterId = document.getElementById('billMeterId').value;
        const billingPeriod = document.getElementById('billingPeriod').value;
        const dueDate = document.getElementById('dueDate').value;

        console.log('[generateBill] Form values:', { meterId, billingPeriod, dueDate });

        if (!meterId || !billingPeriod || !dueDate) {
            showAlert('Vui lòng điền đầy đủ thông tin!', 'warning');
            return;
        }

        const billData = {
            meterId: parseInt(meterId),
            billingPeriod,
            dueDate
        };

        console.log('[generateBill] Sending bill data:', billData);

        const response = await apiCall('/bills/generate', {
            method: 'POST',
            body: JSON.stringify(billData)
        });

        console.log('[generateBill] Response status:', response.status);

        if (response.ok) {
            showAlert('Tạo hóa đơn thành công!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('generateBillModal')).hide();
            clearForm('generateBillForm');
            loadBills();
            loadStatistics();
        } else {
            const errorText = await response.text();
            console.error('[generateBill] Server error:', errorText);
            showAlert('Lỗi: ' + errorText, 'danger');
        }
    } catch (error) {
        console.error('[generateBill] Exception:', error);
        showAlert('Lỗi kết nối: ' + error.message, 'danger');
    }
}

async function viewBillDetail(billId) {
    try {
        const response = await apiCall(`/bills/${billId}`);
        
        if (response.ok) {
            const bill = await response.json();
            
            const content = `
                <div class="row">
                    <div class="col-md-6">
                        <h6><i class="fas fa-file-invoice text-primary me-2"></i>Thông tin hóa đơn</h6>
                        <table class="table table-borderless">
                            <tr><td><strong>Mã hóa đơn:</strong></td><td>#${bill.id}</td></tr>
                            <tr><td><strong>Kỳ hóa đơn:</strong></td><td>${bill.billingPeriod}</td></tr>
                            <tr><td><strong>Ngày tạo:</strong></td><td>${formatDate(bill.createdAt)}</td></tr>
                            <tr><td><strong>Hạn thanh toán:</strong></td><td>${formatDate(bill.dueDate)}</td></tr>
                            <tr><td><strong>Trạng thái:</strong></td><td>
                                <span class="badge ${bill.status === 'PAID' ? 'bg-success' : bill.status === 'OVERDUE' ? 'bg-danger' : 'bg-warning'}">${bill.status}</span>
                            </td></tr>
                        </table>
                    </div>
                    <div class="col-md-6">
                        <h6><i class="fas fa-calculator text-info me-2"></i>Chi tiết tính toán</h6>
                        <table class="table table-borderless">
                            <tr><td><strong>Chỉ số cũ:</strong></td><td>${bill.previousReading || 0} kWh</td></tr>
                            <tr><td><strong>Chỉ số mới:</strong></td><td>${bill.currentReading || 0} kWh</td></tr>
                            <tr><td><strong>Tiêu thụ:</strong></td><td>${bill.unitsConsumed || 0} kWh</td></tr>
                            <tr><td><strong>Đơn giá:</strong></td><td>${formatCurrency(bill.rate || 0)}/kWh</td></tr>
                            <tr><td><strong>Tổng tiền:</strong></td><td class="text-success fw-bold">${formatCurrency(bill.totalAmount || 0)}</td></tr>
                        </table>
                    </div>
                </div>
            `;
            
            document.getElementById('billDetailsContent').innerHTML = content;
            
            // Show/hide pay button based on status
            const payBtn = document.getElementById('payBillBtn');
            if (bill.status === 'PENDING' || bill.status === 'OVERDUE') {
                payBtn.style.display = 'inline-block';
                payBtn.setAttribute('onclick', `payBill(${bill.id})`);
            } else {
                payBtn.style.display = 'none';
            }
            
            const modal = new bootstrap.Modal(document.getElementById('billDetailsModal'));
            modal.show();
        } else {
            showAlert('Không thể tải chi tiết hóa đơn!', 'danger');
        }
    } catch (error) {
        showAlert('Lỗi kết nối: ' + error.message, 'danger');
    }
}

async function payBill(billId) {
    if (!confirm('Bạn có chắc chắn muốn thanh toán hóa đơn này?')) {
        return;
    }

    try {
        const response = await apiCall(`/bills/${billId}/pay`, {
            method: 'PUT'
        });

        if (response.ok) {
            showAlert('Thanh toán hóa đơn thành công!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('billDetailsModal')).hide();
            loadBills();
            loadStatistics();
        } else {
            const errorText = await response.text();
            showAlert('Lỗi: ' + errorText, 'danger');
        }
    } catch (error) {
        showAlert('Lỗi kết nối: ' + error.message, 'danger');
    }
}

// Tariff calculator functions
async function loadTariffsForCalculator() {
    try {
        const response = await fetch(`${API_BASE}/tariffs`);
        const tariffs = await response.json();
        
        const select = document.getElementById('calcTariffId');
        select.innerHTML = '<option value="">Chọn loại biểu giá</option>';
        
        tariffs.forEach(tariff => {
            const now = new Date();
            const validFrom = new Date(tariff.validFrom);
            const validTo = new Date(tariff.validTo);
            const isActive = now >= validFrom && now <= validTo;
            
            if (isActive) {
                select.innerHTML += `<option value="${tariff.id}">Biểu giá #${tariff.id} - ${formatCurrency(tariff.ratePerUnit)}/kWh (${formatDate(tariff.validFrom)} - ${formatDate(tariff.validTo)})</option>`;
            }
        });
    } catch (error) {
        showAlert('Không thể tải danh sách biểu giá!', 'danger');
    }
}

async function calculateElectricityBill() {
    try {
        const kwh = parseFloat(document.getElementById('calcKwh').value);
        const tariffId = document.getElementById('calcTariffId').value;

        if (!kwh || !tariffId) {
            showAlert('Vui lòng điền đầy đủ thông tin!', 'warning');
            return;
        }

        const response = await fetch(`${API_BASE}/tariffs/${tariffId}`);
        const tariff = await response.json();
        
        const amount = kwh * tariff.ratePerUnit;
        const vat = amount * 0.1; // 10% VAT
        const total = amount + vat;
        
        const resultDiv = document.getElementById('calculationResult');
        const detailsDiv = document.getElementById('calculationDetails');
        
        detailsDiv.innerHTML = `
            <table class="table table-sm">
                <tr><td>Số điện tiêu thụ:</td><td class="text-end">${kwh} kWh</td></tr>
                <tr><td>Đơn giá:</td><td class="text-end">${formatCurrency(tariff.ratePerUnit)}/kWh</td></tr>
                <tr><td>Tiền điện:</td><td class="text-end">${formatCurrency(amount)}</td></tr>
                <tr><td>VAT (10%):</td><td class="text-end">${formatCurrency(vat)}</td></tr>
                <tr class="table-active"><td><strong>Tổng cộng:</strong></td><td class="text-end"><strong>${formatCurrency(total)}</strong></td></tr>
            </table>
        `;
        
        resultDiv.style.display = 'block';
    } catch (error) {
        showAlert('Lỗi khi tính toán: ' + error.message, 'danger');
    }
}

// Chart functions
let consumptionChart = null;

async function viewChart() {
    try {
        const response = await apiCall('/consumptions');
        const consumptions = await response.json();
        
        if (consumptions.length === 0) {
            showAlert('Chưa có dữ liệu tiêu thụ để hiển thị biểu đồ!', 'warning');
            return;
        }

        // Group data by month
        const monthlyData = {};
        consumptions.forEach(consumption => {
            if (!consumption.recordedDate) return;
            
            try {
                const month = consumption.recordedDate.substring(0, 7); // YYYY-MM
                if (!monthlyData[month]) {
                    monthlyData[month] = 0;
                }
                monthlyData[month] += consumption.unitsConsumed || 0;
            } catch (error) {
                console.warn('Error processing consumption date:', consumption.recordedDate, error);
            }
        });

        const months = Object.keys(monthlyData).sort();
        const values = months.map(month => monthlyData[month]);

        const chartContainer = document.getElementById('chartContainer');
        chartContainer.style.display = 'block';

        const ctx = document.getElementById('consumptionChart').getContext('2d');

        // Destroy existing chart if it exists
        if (consumptionChart) {
            consumptionChart.destroy();
        }

        consumptionChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: months.map(month => {
                    const [year, monthNum] = month.split('-');
                    return `${monthNum}/${year}`;
                }),
                datasets: [{
                    label: 'Tiêu thụ điện (kWh)',
                    data: values,
                    borderColor: 'rgb(75, 192, 192)',
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    tension: 0.1,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    title: {
                        display: true,
                        text: 'Biểu đồ tiêu thụ điện theo thời gian'
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: 'kWh'
                        }
                    },
                    x: {
                        title: {
                            display: true,
                            text: 'Tháng/Năm'
                        }
                    }
                }
            }
        });

        showAlert('Hiển thị biểu đồ thành công!', 'success');
    } catch (error) {
        showAlert('Lỗi khi tải biểu đồ: ' + error.message, 'danger');
    }
}

// Statistics loading function
async function loadStatistics() {
    try {
        // Load all data for statistics
        const [billsResponse, metersResponse, consumptionResponse] = await Promise.all([
            apiCall('/bills').catch(() => ({ ok: false })),
            apiCall('/meters').catch(() => ({ ok: false })),
            apiCall('/consumptions').catch(() => ({ ok: false }))
        ]);

        let totalBills = 0, totalMeters = 0, totalConsumption = 0, unpaidAmount = 0;

        if (billsResponse.ok) {
            const bills = await billsResponse.json();
            totalBills = bills.length;
            unpaidAmount = bills
                .filter(bill => bill.status !== 'PAID')
                .reduce((sum, bill) => sum + (bill.totalAmount || 0), 0);
        }

        if (metersResponse.ok) {
            const meters = await metersResponse.json();
            totalMeters = meters.length;
        }

        if (consumptionResponse.ok) {
            const consumptions = await consumptionResponse.json();
            totalConsumption = consumptions.reduce((sum, c) => sum + (c.unitsConsumed || 0), 0);
        }

        // Update UI
        document.getElementById('totalBills').textContent = formatNumber(totalBills);
        document.getElementById('totalMeters').textContent = formatNumber(totalMeters);
        document.getElementById('totalConsumption').textContent = formatNumber(totalConsumption);
        document.getElementById('unpaidAmount').textContent = formatCurrency(unpaidAmount).replace('₫', '');
    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

// Export data function
function exportData(type) {
    // This is a simplified version - in real implementation, you would generate and download actual Excel files
    const data = {
        bills: 'Danh sách hóa đơn',
        meters: 'Danh sách đồng hồ điện',
        consumption: 'Dữ liệu tiêu thụ',
        tariffs: 'Bảng biểu giá'
    };
    
    showAlert(`Xuất dữ liệu ${data[type]} thành công! (Demo)`, 'success');
}

// Initialize statistics on main content show
function showMainContent() {
    document.getElementById('authSection').style.display = 'none';
    document.getElementById('mainContent').classList.add('show');
    
    // Update user info
    document.getElementById('userInfo').textContent = `${currentUser.fullName}`;
    
    // Load initial data and statistics
    loadBills();
    loadStatistics();
}

// Tariff management functions
async function initSampleTariffs() {
    try {
        const response = await fetch(`${API_BASE}/tariffs/init-sample-data`, {
            method: 'POST'
        });
        
        if (response.ok) {
            const message = await response.text();
            showAlert(message, 'success');
            loadTariffs(); // Reload tariffs after creating sample data
        } else {
            const errorText = await response.text();
            showAlert('Lỗi: ' + errorText, 'danger');
        }
    } catch (error) {
        console.error('Error initializing sample tariffs:', error);
        showAlert('Lỗi kết nối: ' + error.message, 'danger');
    }
}

async function useTariffForCalculation(tariffId) {
    // Load this tariff for the calculation modal
    try {
        const response = await fetch(`${API_BASE}/tariffs/${tariffId}`);
        if (response.ok) {
            const tariff = await response.json();
            
            // Pre-fill calculation form with this tariff
            document.getElementById('calcTariffId').value = tariffId;
            
            // Show calculation modal
            const calculationModal = new bootstrap.Modal(document.getElementById('calculatorModal'));
            calculationModal.show();
            
            showAlert(`Đã chọn biểu giá ${formatCurrency(tariff.ratePerUnit)}/kWh`, 'info');
        } else {
            showAlert('Không thể tải thông tin biểu giá!', 'danger');
        }
    } catch (error) {
        console.error('Error loading tariff for calculation:', error);
        showAlert('Lỗi kết nối: ' + error.message, 'danger');
    }
}

async function viewTariffDetail(tariffId) {
    try {
        const response = await fetch(`${API_BASE}/tariffs/${tariffId}`);
        if (response.ok) {
            const tariff = await response.json();
            
            const content = `
                <div class="row">
                    <div class="col-md-6">
                        <h6><i class="fas fa-info-circle text-primary me-2"></i>Thông tin cơ bản</h6>
                        <table class="table table-borderless">
                            <tr><td><strong>ID:</strong></td><td>#${tariff.id}</td></tr>
                            <tr><td><strong>Đơn giá:</strong></td><td class="text-success fw-bold">${formatCurrency(tariff.ratePerUnit)}/kWh</td></tr>
                        </table>
                    </div>
                    <div class="col-md-6">
                        <h6><i class="fas fa-calendar text-info me-2"></i>Thời gian hiệu lực</h6>
                        <table class="table table-borderless">
                            <tr><td><strong>Từ ngày:</strong></td><td>${formatDate(tariff.validFrom)}</td></tr>
                            <tr><td><strong>Đến ngày:</strong></td><td>${formatDate(tariff.validTo)}</td></tr>
                        </table>
                    </div>
                </div>
            `;
            
            // Create and show modal
            const modalHtml = `
                <div class="modal fade" id="tariffDetailModal" tabindex="-1">
                    <div class="modal-dialog modal-lg">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title"><i class="fas fa-bolt text-warning me-2"></i>Chi tiết biểu giá điện</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                ${content}
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                                <button type="button" class="btn btn-primary" onclick="useTariffForCalculation(${tariffId})" data-bs-dismiss="modal">
                                    <i class="fas fa-calculator me-1"></i>Sử dụng tính toán
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            
            // Remove existing modal if any
            const existingModal = document.getElementById('tariffDetailModal');
            if (existingModal) {
                existingModal.remove();
            }
            
            // Add modal to body
            document.body.insertAdjacentHTML('beforeend', modalHtml);
            
            // Show modal
            const modal = new bootstrap.Modal(document.getElementById('tariffDetailModal'));
            modal.show();
            
        } else {
            showAlert('Không thể tải thông tin biểu giá!', 'danger');
        }
    } catch (error) {
        console.error('Error viewing tariff detail:', error);
        showAlert('Lỗi kết nối: ' + error.message, 'danger');
    }
}

// Load all statistics for dashboard
async function loadStatistics() {
    try {
        // Load bills and update statistics
        const billsResponse = await apiCall('/bills');
        if (billsResponse.ok) {
            const bills = await billsResponse.json();
            updateStatistics(bills, 'bills');
        }
        
        // Load meters and update statistics
        const metersResponse = await apiCall('/meters');
        if (metersResponse.ok) {
            const meters = await metersResponse.json();
            updateStatistics(meters, 'meters');
        }
        
        // Load consumption and update statistics
        const consumptionResponse = await apiCall('/consumptions');
        if (consumptionResponse.ok) {
            const consumptions = await consumptionResponse.json();
            updateStatistics(consumptions, 'consumption');
        }
    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

// Excel export functions
async function exportBillsToExcel() {
    try {
        showLoading('Đang xuất file Excel...');
        
        // Tạo URL với các tham số
        const url = new URL(`${API_BASE}/bills/export/excel`);
        
        // Có thể thêm tham số lọc ở đây
        // url.searchParams.append('customerId', currentUser.id);
        
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                ...(authToken && { 'Authorization': `Bearer ${authToken}` })
            }
        });
        
        if (response.ok) {
            const blob = await response.blob();
            
            // Tạo link download
            const downloadUrl = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = downloadUrl;
            
            // Lấy tên file từ header hoặc tạo tên mặc định
            const contentDisposition = response.headers.get('Content-Disposition');
            let fileName = 'HoaDon_TienDien.xlsx';
            if (contentDisposition) {
                const fileNameMatch = contentDisposition.match(/filename="(.+)"/);
                if (fileNameMatch) {
                    fileName = fileNameMatch[1];
                }
            }
            
            link.download = fileName;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(downloadUrl);
            
            showAlert('Xuất file Excel thành công!', 'success');
        } else {
            throw new Error('Không thể xuất file Excel');
        }
    } catch (error) {
        console.error('Excel export error:', error);
        showAlert('Lỗi khi xuất file Excel: ' + error.message, 'danger');
    } finally {
        hideLoading();
    }
}

// Xuất hóa đơn tháng hiện tại
async function exportCurrentMonthBills() {
    try {
        showLoading('Đang xuất hóa đơn tháng này...');
        
        const now = new Date();
        const startDate = new Date(now.getFullYear(), now.getMonth(), 1).toISOString().split('T')[0];
        const endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0).toISOString().split('T')[0];
        
        const url = new URL(`${API_BASE}/bills/export/excel`);
        url.searchParams.append('startDate', startDate);
        url.searchParams.append('endDate', endDate);
        
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                ...(authToken && { 'Authorization': `Bearer ${authToken}` })
            }
        });
        
        if (response.ok) {
            const blob = await response.blob();
            const downloadUrl = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = downloadUrl;
            
            const monthYear = now.toLocaleDateString('vi-VN', { month: 'long', year: 'numeric' });
            link.download = `HoaDon_${monthYear.replace(' ', '_')}.xlsx`;
            
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(downloadUrl);
            
            showAlert(`Xuất hóa đơn tháng ${monthYear} thành công!`, 'success');
        } else {
            throw new Error('Không thể xuất file Excel');
        }
    } catch (error) {
        console.error('Excel export error:', error);
        showAlert('Lỗi khi xuất file Excel: ' + error.message, 'danger');
    } finally {
        hideLoading();
    }
}

// Xuất hóa đơn của người dùng hiện tại
async function exportMyBills() {
    try {
        if (!currentUser?.id) {
            showAlert('Không tìm thấy thông tin người dùng!', 'warning');
            return;
        }
        
        showLoading('Đang xuất hóa đơn của bạn...');
        
        const url = new URL(`${API_BASE}/bills/export/excel`);
        url.searchParams.append('customerId', currentUser.id);
        
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                ...(authToken && { 'Authorization': `Bearer ${authToken}` })
            }
        });
        
        if (response.ok) {
            const blob = await response.blob();
            const downloadUrl = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = downloadUrl;
            
            link.download = `HoaDon_${currentUser.fullName.replace(/\s+/g, '_')}.xlsx`;
            
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(downloadUrl);
            
            showAlert('Xuất hóa đơn cá nhân thành công!', 'success');
        } else {
            throw new Error('Không thể xuất file Excel');
        }
    } catch (error) {
        console.error('Excel export error:', error);
        showAlert('Lỗi khi xuất file Excel: ' + error.message, 'danger');
    } finally {
        hideLoading();
    }
}

async function exportBillsToExcelWithFilter() {
    try {
        // Hiển thị modal để chọn bộ lọc
        const modalHtml = `
            <div class="modal fade" id="exportFilterModal" tabindex="-1">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">
                                <i class="fas fa-file-excel me-2"></i>Xuất Excel với bộ lọc
                            </h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <form id="exportFilterForm">
                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <label for="exportStartDate" class="form-label">
                                            <i class="fas fa-calendar me-1"></i>Từ ngày
                                        </label>
                                        <input type="date" class="form-control" id="exportStartDate">
                                    </div>
                                    <div class="col-md-6 mb-3">
                                        <label for="exportEndDate" class="form-label">
                                            <i class="fas fa-calendar me-1"></i>Đến ngày
                                        </label>
                                        <input type="date" class="form-control" id="exportEndDate">
                                    </div>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">
                                        <i class="fas fa-filter me-1"></i>Tùy chọn lọc
                                    </label>
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" id="exportCurrentUserOnly" checked>
                                        <label class="form-check-label" for="exportCurrentUserOnly">
                                            <i class="fas fa-user me-1"></i>Chỉ xuất hóa đơn của tôi
                                        </label>
                                    </div>
                                </div>
                                <div class="mb-3">
                                    <label for="exportPreset" class="form-label">
                                        <i class="fas fa-clock me-1"></i>Bộ lọc nhanh
                                    </label>
                                    <select class="form-select" id="exportPreset" onchange="applyExportPreset()">
                                        <option value="">Chọn bộ lọc nhanh...</option>
                                        <option value="thisMonth">Tháng này</option>
                                        <option value="lastMonth">Tháng trước</option>
                                        <option value="thisQuarter">Quý này</option>
                                        <option value="thisYear">Năm này</option>
                                        <option value="last30days">30 ngày qua</option>
                                        <option value="last90days">90 ngày qua</option>
                                    </select>
                                </div>
                                <div class="alert alert-info">
                                    <i class="fas fa-info-circle me-2"></i>
                                    <strong>Lưu ý:</strong> File Excel sẽ bao gồm thông tin chi tiết và thống kê tổng hợp.
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                <i class="fas fa-times me-2"></i>Hủy
                            </button>
                            <button type="button" class="btn btn-success" onclick="executeExcelExport()">
                                <i class="fas fa-file-excel me-2"></i>Xuất Excel
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        // Thêm modal vào DOM nếu chưa có
        if (!document.getElementById('exportFilterModal')) {
            document.body.insertAdjacentHTML('beforeend', modalHtml);
        }
        
        // Hiển thị modal
        const modal = new bootstrap.Modal(document.getElementById('exportFilterModal'));
        modal.show();
        
    } catch (error) {
        console.error('Error showing export filter modal:', error);
        showAlert('Lỗi khi hiển thị bộ lọc xuất Excel', 'danger');
    }
}

// Áp dụng bộ lọc nhanh
function applyExportPreset() {
    const preset = document.getElementById('exportPreset').value;
    const startDateInput = document.getElementById('exportStartDate');
    const endDateInput = document.getElementById('exportEndDate');
    
    if (!preset) {
        startDateInput.value = '';
        endDateInput.value = '';
        return;
    }
    
    const now = new Date();
    let startDate, endDate;
    
    switch (preset) {
        case 'thisMonth':
            startDate = new Date(now.getFullYear(), now.getMonth(), 1);
            endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0);
            break;
            
        case 'lastMonth':
            startDate = new Date(now.getFullYear(), now.getMonth() - 1, 1);
            endDate = new Date(now.getFullYear(), now.getMonth(), 0);
            break;
            
        case 'thisQuarter':
            const quarterStart = Math.floor(now.getMonth() / 3) * 3;
            startDate = new Date(now.getFullYear(), quarterStart, 1);
            endDate = new Date(now.getFullYear(), quarterStart + 3, 0);
            break;
            
        case 'thisYear':
            startDate = new Date(now.getFullYear(), 0, 1);
            endDate = new Date(now.getFullYear(), 11, 31);
            break;
            
        case 'last30days':
            startDate = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
            endDate = new Date();
            break;
            
        case 'last90days':
            startDate = new Date(now.getTime() - 90 * 24 * 60 * 60 * 1000);
            endDate = new Date();
            break;
            
        default:
            return;
    }
    
    startDateInput.value = startDate.toISOString().split('T')[0];
    endDateInput.value = endDate.toISOString().split('T')[0];
}

async function executeExcelExport() {
    try {
        showLoading('Đang xuất file Excel...');
        
        const startDate = document.getElementById('exportStartDate').value;
        const endDate = document.getElementById('exportEndDate').value;
        const currentUserOnly = document.getElementById('exportCurrentUserOnly').checked;
        
        // Tạo URL với các tham số
        const url = new URL(`${API_BASE}/bills/export/excel`);
        
        if (currentUserOnly && currentUser?.id) {
            url.searchParams.append('customerId', currentUser.id);
        }
        
        if (startDate) {
            url.searchParams.append('startDate', startDate);
        }
        
        if (endDate) {
            url.searchParams.append('endDate', endDate);
        }
        
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                ...(authToken && { 'Authorization': `Bearer ${authToken}` })
            }
        });
        
        if (response.ok) {
            const blob = await response.blob();
            
            // Tạo link download
            const downloadUrl = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = downloadUrl;
            
            // Tạo tên file với thông tin bộ lọc
            let fileName = 'HoaDon_TienDien';
            if (startDate && endDate) {
                fileName += `_${startDate}_${endDate}`;
            }
            if (currentUserOnly) {
                fileName += '_CaNhan';
            }
            fileName += '.xlsx';
            
            link.download = fileName;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(downloadUrl);
            
            // Đóng modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('exportFilterModal'));
            modal.hide();
            
            showAlert('Xuất file Excel thành công!', 'success');
        } else {
            throw new Error('Không thể xuất file Excel');
        }
    } catch (error) {
        console.error('Excel export error:', error);
        showAlert('Lỗi khi xuất file Excel: ' + error.message, 'danger');
    } finally {
        hideLoading();
    }
}
