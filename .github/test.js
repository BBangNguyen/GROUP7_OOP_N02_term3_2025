function classifyAge(age) {
/**
* Hàm phân loại độ tuổi theo tiêu chuẩn Việt Nam.
*
* Đầu vào:
* age (number): Số tuổi của người dùng, phải là số nguyên dương.
*
* Đầu ra:
* string: Chuỗi phân loại độ tuổi dựa trên độ tuổi đã nhập.
*
* Các phân loại độ tuổi:
* - Trẻ sơ sinh: từ 1 đến 5 tuổi
* - Trẻ em: từ 6 đến 10 tuổi
* - Thiếu niên: từ 11 đến 15 tuổi
* - Thanh niên: từ 16 đến 24 tuổi
* - Người trưởng thành: từ 25 đến 59 tuổi
* - Người già: từ 60 tuổi trở lên
*/

// Kiểm tra nếu đầu vào không hợp lệ
if (typeof age !== 'number' || age <= 0) {
return "Số tuổi phải là số nguyên dương";
}

if (age > 150) {
return "Số tuổi quá lớn";
}

// Phân loại theo độ tuổi
if (age >= 1 && age <= 5) {
return "Trẻ sơ sinh";
} else if (age >= 6 && age <= 10) {
return "Trẻ em";
} else if (age >= 11 && age <= 15) {
return "Thiếu niên";
} else if (age >= 16 && age <= 24) {
return "Thanh niên";
} else if (age >= 25 && age <= 59) {
return "Người trưởng thành";
} else {
return "Người già";
}
}

function runTestCases() {
let totalTests = 0;
let passedTests = 0;

const testResults = [];

// Các trường hợp kiểm thử hợp lệ
const testCases = [
{ age: 1, expected: "Trẻ sơ sinh" },
{ age: 5, expected: "Trẻ sơ sinh" },
{ age: 6, expected: "Trẻ em" },
{ age: 10, expected: "Trẻ em" },
{ age: 11, expected: "Thiếu niên" },
{ age: 15, expected: "Thiếu niên" },
{ age: 16, expected: "Thanh niên" },
{ age: 24, expected: "Thanh niên" },
{ age: 25, expected: "Người trưởng thành" },
{ age: 59, expected: "Người trưởng thành" },
{ age: 60, expected: "Người già" },
{ age: 150, expected: "Người già" }
];

// Các trường hợp kiểm thử không hợp lệ
const invalidTestCases = [
{ age: 0, expected: "Số tuổi phải là số nguyên dương" },
{ age: -1, expected: "Số tuổi phải là số nguyên dương" },
{ age: 151, expected: "Số tuổi quá lớn" },
{ age: "20", expected: "Số tuổi phải là số nguyên dương" },
{ age: null, expected: "Số tuổi phải là số nguyên dương" },
{ age: undefined, expected: "Số tuổi phải là số nguyên dương" }
];

// Kiểm thử với các trường hợp hợp lệ
testCases.forEach(testCase => {
const result = classifyAge(testCase.age);
const isTestPassed = result === testCase.expected;
if (isTestPassed) passedTests++;
totalTests++;

testResults.push({
age: testCase.age,
result: result,
expected: testCase.expected,
passed: isTestPassed
});
});

// Kiểm thử với các trường hợp không hợp lệ
invalidTestCases.forEach(testCase => {
const result = classifyAge(testCase.age);
const isTestPassed = result === testCase.expected;
if (isTestPassed) passedTests++;
totalTests++;

testResults.push({
age: testCase.age,
result: result,
expected: testCase.expected,
passed: isTestPassed
});
});

// Thống kê kết quả kiểm thử
const passRate = (passedTests / totalTests) * 100;

// In kết quả
console.log("Kết quả kiểm thử:");
testResults.forEach(test => {
console.log(`Tuổi: ${test.age}, Kết quả thực tế: ${test.result}, Kết quả mong đợi: ${test.expected}, Thành công: ${test.passed}`);
});

console.log(`\nTổng số trường hợp kiểm thử: ${totalTests}`);
console.log(`Số trường hợp kiểm thử thành công: ${passedTests}`);
console.log(`Tỷ lệ thành công: ${passRate.toFixed(2)}%`);
}

// Chạy kiểm thử
runTestCases();