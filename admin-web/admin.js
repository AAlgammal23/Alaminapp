const firebaseConfig = {
    apiKey: "YOUR_API_KEY",
    authDomain: "YOUR_PROJECT.firebaseapp.com",
    projectId: "YOUR_PROJECT_ID",
    storageBucket: "YOUR_PROJECT.appspot.com",
    messagingSenderId: "YOUR_SENDER_ID",
    appId: "YOUR_APP_ID"
};

firebase.initializeApp(firebaseConfig);
const db = firebase.firestore();
const auth = firebase.auth();

auth.signInWithEmailAndPassword('admin@alamin-pharma.com', 'password')
    .then(() => loadDashboard())
    .catch(() => showLogin());

function showLogin() {
    document.getElementById('app').innerHTML = `
        <div style="max-width:400px;margin:100px auto;">
            <h2>تسجيل الدخول</h2>
            <input id="email" type="email" placeholder="البريد" style="width:100%;padding:10px;margin:10px 0;">
            <input id="password" type="password" placeholder="كلمة السر" style="width:100%;padding:10px;margin:10px 0;">
            <button onclick="login()" class="btn btn-primary">دخول</button>
        </div>
    `;
}

function login() {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    auth.signInWithEmailAndPassword(email, password)
        .then(() => loadDashboard())
        .catch(err => alert('خطأ في البيانات'));
}

function loadDashboard() {
    const app = document.getElementById('app');
    app.innerHTML = `
        <header>
            <h1>💊 صيدلية الأمين الحديثة</h1>
            <button onclick="logout()" class="btn btn-danger">تسجيل خروج</button>
        </header>
        <div class="stats" id="stats"></div>
        <div class="section">
            <h2>المنتجات</h2>
            <button onclick="addProduct()" class="btn btn-primary">➕ إضافة منتج</button>
            <table id="productsTable">
                <thead><tr><th>الاسم</th><th>السعر</th><th>القسم</th><th>الإجراءات</th></tr></thead>
                <tbody id="productsBody"></tbody>
            </table>
        </div>
        <div class="section">
            <h2>الأصناف</h2>
            <button onclick="addCategory()" class="btn btn-primary">➕ إضافة صنف</button>
            <table id="categoriesTable">
                <thead><tr><th>الاسم</th><th>الإجراءات</th></tr></thead>
                <tbody id="categoriesBody"></tbody>
            </table>
        </div>
        <div class="section">
            <h2>معلومات التواصل</h2>
            <div id="contactForm"></div>
        </div>
    `;
    loadProducts();
    loadCategories();
    loadStats();
    loadContactInfo();
}

function loadProducts() {
    db.collection('pharmacy_products').orderBy('createdAt').onSnapshot(snapshot => {
        const body = document.getElementById('productsBody');
        body.innerHTML = '';
        snapshot.forEach(doc => {
            const data = doc.data();
            body.innerHTML += `
                <tr>
                    <td>${data.name}</td>
                    <td>${data.price} ريال</td>
                    <td>${data.category}</td>
                    <td>
                        <button onclick="editProduct('${doc.id}')" class="btn btn-warning">تعديل</button>
                        <button onclick="deleteProduct('${doc.id}')" class="btn btn-danger">حذف</button>
                    </td>
                </tr>
            `;
        });
    });
}

function loadCategories() {
    db.collection('pharmacy_categories').orderBy('order').onSnapshot(snapshot => {
        const body = document.getElementById('categoriesBody');
        body.innerHTML = '';
        snapshot.forEach(doc => {
            const data = doc.data();
            body.innerHTML += `
                <tr>
                    <td>${data.icon} ${data.name}</td>
                    <td>
                        <button onclick="editCategory('${doc.id}')" class="btn btn-warning">تعديل</button>
                        <button onclick="deleteCategory('${doc.id}')" class="btn btn-danger">حذف</button>
                    </td>
                </tr>
            `;
        });
    });
}

function loadStats() {
    const statsDiv = document.getElementById('stats');
    db.collection('pharmacy_products').get().then(snap => {
        const count = snap.size;
        statsDiv.innerHTML = `
            <div class="stat-card"><h3>المنتجات</h3><p>${count}</p></div>
            <div class="stat-card"><h3>الأصناف</h3><p>0</p></div>
            <div class="stat-card"><h3>الطلبات</h3><p>0</p></div>
        `;
    });
}

function loadContactInfo() {
    db.collection('pharmacy_contacts').doc('info').get().then(doc => {
        if (doc.exists) {
            const data = doc.data();
            document.getElementById('contactForm').innerHTML = `
                <input id="whatsapp" value="${data.whatsapp}" placeholder="واتساب" style="margin:5px;padding:8px;width:200px;">
                <input id="phone" value="${data.phone}" placeholder="هاتف" style="margin:5px;padding:8px;width:200px;">
                <input id="email" value="${data.email}" placeholder="بريد" style="margin:5px;padding:8px;width:200px;">
                <input id="address" value="${data.address}" placeholder="عنوان" style="margin:5px;padding:8px;width:200px;">
                <input id="facebook" value="${data.facebook}" placeholder="فيسبوك" style="margin:5px;padding:8px;width:200px;">
                <input id="hours" value="${data.workingHours}" placeholder="ساعات العمل" style="margin:5px;padding:8px;width:200px;">
                <button onclick="updateContact()" class="btn btn-primary">تحديث</button>
            `;
        }
    });
}

function updateContact() {
    const data = {
        whatsapp: document.getElementById('whatsapp').value,
        phone: document.getElementById('phone').value,
        email: document.getElementById('email').value,
        address: document.getElementById('address').value,
        facebook: document.getElementById('facebook').value,
        workingHours: document.getElementById('hours').value
    };
    db.collection('pharmacy_contacts').doc('info').set(data)
        .then(() => alert('تم التحديث'))
        .catch(err => alert('فشل التحديث'));
}

function addProduct() {
    const name = prompt('اسم المنتج');
    if (name) {
        const price = prompt('السعر');
        const category = prompt('القسم');
        db.collection('pharmacy_products').add({
            name, price: parseFloat(price), category,
            imageUrl: '', description: '', isOffer: false, offerPrice: 0,
            createdAt: firebase.firestore.FieldValue.serverTimestamp()
        });
    }
}

function deleteProduct(id) {
    if (confirm('هل أنت متأكد؟')) {
        db.collection('pharmacy_products').doc(id).delete();
    }
}

function addCategory() {
    const name = prompt('اسم الصنف');
    if (name) {
        const icon = prompt('الأيقونة (رمز)');
        db.collection('pharmacy_categories').add({
            name, icon, order: Date.now()
        });
    }
}

function deleteCategory(id) {
    if (confirm('هل أنت متأكد؟')) {
        db.collection('pharmacy_categories').doc(id).delete();
    }
}

function logout() {
    auth.signOut().then(() => showLogin());
}
