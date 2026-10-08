# 店家端 Flutter App

此資料夾提供店家端 App 的起始介面：登入頁、今日預約清單、狀態更新選單與統計摘要。

本機尚未安裝 Flutter SDK，完成安裝後請在此資料夾執行：

```powershell
flutter create --platforms=android,ios .
flutter run
```

下一步可將登入接至 `POST /api/auth/login`，並將預約清單接至 `GET /api/reservations?date=YYYY-MM-DD`。
