import 'dart:convert';
import 'package:http/http.dart' as http;
import 'reservation_model.dart';

class ApiService {
  static String baseUrl = 'http://localhost:8080/api';
  static String? _authToken;

  static String? get authToken => _authToken;

  static void setAuthToken(String token) {
    _authToken = token;
  }

  static Map<String, String> get _headers {
    final map = {'Content-Type': 'application/json'};
    if (_authToken != null && _authToken!.isNotEmpty) {
      map['Authorization'] = 'Bearer $_authToken';
    }
    return map;
  }

  static Future<bool> login(String username, String password) async {
    final response = await http.post(
      Uri.parse('$baseUrl/auth/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'username': username, 'password': password}),
    );

    if (response.statusCode == 200) {
      final body = jsonDecode(response.body);
      _authToken = body['accessToken'];
      return true;
    } else {
      final body = jsonDecode(response.body);
      throw Exception(body['message'] ?? '登入失敗，請檢查帳號密碼');
    }
  }

  static Future<List<ReservationItem>> fetchReservations(String date) async {
    final uri = Uri.parse('$baseUrl/reservations').replace(queryParameters: {'date': date});
    final response = await http.get(uri, headers: _headers);

    if (response.statusCode == 200) {
      final List data = jsonDecode(utf8.decode(response.bodyBytes));
      return data.map((e) => ReservationItem.fromJson(e)).toList();
    } else {
      throw Exception('無法取得預約列表 (${response.statusCode})');
    }
  }

  static Future<ReservationItem> updateStatus(int id, String newStatus) async {
    final uri = Uri.parse('$baseUrl/reservations/$id/status').replace(queryParameters: {'status': newStatus});
    final response = await http.patch(uri, headers: _headers);

    if (response.statusCode == 200) {
      final data = jsonDecode(utf8.decode(response.bodyBytes));
      return ReservationItem.fromJson(data);
    } else {
      throw Exception('更新狀態失敗');
    }
  }

  static Future<StatisticsItem> fetchTodayStatistics() async {
    final response = await http.get(Uri.parse('$baseUrl/statistics/today'), headers: _headers);
    if (response.statusCode == 200) {
      final data = jsonDecode(utf8.decode(response.bodyBytes));
      return StatisticsItem.fromJson(data);
    } else {
      throw Exception('無法載入統計數據');
    }
  }

  static Future<void> createReservation({
    required String name,
    required String phone,
    required String date,
    required String time,
    required int guestCount,
    String? recommendedBy,
    String? notes,
  }) async {
    final response = await http.post(
      Uri.parse('$baseUrl/public/reservations'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'name': name,
        'phone': phone,
        'reservationDate': date,
        'reservationTime': time,
        'guestCount': guestCount,
        'recommendedBy': recommendedBy,
        'notes': notes,
      }),
    );

    if (response.statusCode != 200 && response.statusCode != 201) {
      final body = jsonDecode(utf8.decode(response.bodyBytes));
      throw Exception(body['message'] ?? '建立預約失敗');
    }
  }
}
