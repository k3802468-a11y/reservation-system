import 'package:flutter/material.dart';
import 'api_service.dart';
import 'reservation_model.dart';

void main() => runApp(const ReservationMerchantApp());

class ReservationMerchantApp extends StatelessWidget {
  const ReservationMerchantApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '煙深雨靜 | 店家預約管理',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xff506958),
          brightness: Brightness.light,
        ),
      ),
      darkTheme: ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xff506958),
          brightness: Brightness.dark,
        ),
      ),
      home: const LoginPage(),
    );
  }
}

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final _formKey = GlobalKey<FormState>();
  final _username = TextEditingController(text: 'admin');
  final _password = TextEditingController();
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void dispose() {
    _username.dispose();
    _password.dispose();
    super.dispose();
  }

  Future<void> _handleLogin() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      await ApiService.login(_username.text.trim(), _password.text);
      if (!mounted) return;
      Navigator.of(context).pushReplacement(
        MaterialPageRoute(builder: (_) => const DashboardPage()),
      );
    } catch (e) {
      setState(() {
        _errorMessage = e.toString().replaceAll('Exception: ', '');
      });
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24.0),
          child: ConstrainedBox(
            constraints: const BoxConstraints(maxWidth: 400),
            child: Card(
              elevation: 4,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              child: Padding(
                padding: const EdgeInsets.all(32.0),
                child: Form(
                  key: _formKey,
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      const Icon(Icons.storefront_rounded, size: 60, color: Color(0xff506958)),
                      const SizedBox(height: 12),
                      Text(
                        '煙深雨靜',
                        textAlign: TextAlign.center,
                        style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                              fontWeight: FontWeight.bold,
                              color: const Color(0xff506958),
                            ),
                      ),
                      Text(
                        '店家預約管理系統',
                        textAlign: TextAlign.center,
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(color: Colors.grey[600]),
                      ),
                      const SizedBox(height: 32),
                      if (_errorMessage != null)
                        Container(
                          padding: const EdgeInsets.all(12),
                          margin: const EdgeInsets.only(bottom: 16),
                          decoration: BoxDecoration(
                            color: Colors.red[50],
                            borderRadius: BorderRadius.circular(8),
                            border: Border.all(color: Colors.red.shade200),
                          ),
                          child: Text(_errorMessage!, style: const TextStyle(color: Colors.red)),
                        ),
                      TextFormField(
                        controller: _username,
                        decoration: const InputDecoration(
                          labelText: '管理員帳號',
                          prefixIcon: Icon(Icons.person_outline),
                          border: OutlineInputBorder(),
                        ),
                        validator: (v) => v == null || v.isEmpty ? '請輸入帳號' : null,
                      ),
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _password,
                        obscureText: true,
                        decoration: const InputDecoration(
                          labelText: '密碼',
                          prefixIcon: Icon(Icons.lock_outline),
                          border: OutlineInputBorder(),
                        ),
                        validator: (v) => v == null || v.isEmpty ? '請輸入密碼' : null,
                      ),
                      const SizedBox(height: 24),
                      FilledButton(
                        onPressed: _isLoading ? null : _handleLogin,
                        style: FilledButton.styleFrom(
                          padding: const EdgeInsets.symmetric(vertical: 14),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                        ),
                        child: _isLoading
                            ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
                            : const Text('登入管理後台', style: TextStyle(fontSize: 16)),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class DashboardPage extends StatefulWidget {
  const DashboardPage({super.key});

  @override
  State<DashboardPage> createState() => _DashboardPageState();
}

class _DashboardPageState extends State<DashboardPage> {
  DateTime _selectedDate = DateTime.now();
  List<ReservationItem> _allReservations = [];
  StatisticsItem? _todayStats;
  bool _isLoading = false;
  String _selectedFilter = 'ALL';
  String _searchQuery = '';

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  String _formatDate(DateTime date) {
    return "${date.year}-${date.month.toString().padLeft(2, '0')}-${date.day.toString().padLeft(2, '0')}";
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    try {
      final formatted = _formatDate(_selectedDate);
      final list = await ApiService.fetchReservations(formatted);
      StatisticsItem? stats;
      try {
        stats = await ApiService.fetchTodayStatistics();
      } catch (_) {}

      setState(() {
        _allReservations = list;
        _todayStats = stats;
      });
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(e.toString().replaceAll('Exception: ', ''))),
        );
      }
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  Future<void> _updateStatus(ReservationItem item, String status) async {
    try {
      final updated = await ApiService.updateStatus(item.id, status);
      setState(() {
        item.status = updated.status;
      });
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('已成功更新 #${item.id} 狀態為 ${item.statusDisplay}')),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('狀態更新失敗: $e')),
        );
      }
    }
  }

  List<ReservationItem> get _filteredReservations {
    return _allReservations.where((item) {
      final matchesStatus = _selectedFilter == 'ALL' || item.status == _selectedFilter;
      final matchesSearch = _searchQuery.isEmpty ||
          item.customerName.contains(_searchQuery) ||
          item.customerPhone.contains(_searchQuery);
      return matchesStatus && matchesSearch;
    }).toList();
  }

  void _showAddReservationDialog() {
    final nameCtrl = TextEditingController();
    final phoneCtrl = TextEditingController();
    final guestCtrl = TextEditingController(text: '2');
    final timeCtrl = TextEditingController(text: '18:00');
    final noteCtrl = TextEditingController();
    final recCtrl = TextEditingController();

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('手動新增預約'),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(controller: nameCtrl, decoration: const InputDecoration(labelText: '顧客姓名 *')),
              TextField(controller: phoneCtrl, decoration: const InputDecoration(labelText: '聯絡電話 *')),
              TextField(controller: timeCtrl, decoration: const InputDecoration(labelText: '時間 (如 18:30) *')),
              TextField(controller: guestCtrl, keyboardType: TextInputType.number, decoration: const InputDecoration(labelText: '人數 *')),
              TextField(controller: recCtrl, decoration: const InputDecoration(labelText: '推薦店員')),
              TextField(controller: noteCtrl, decoration: const InputDecoration(labelText: '備註')),
            ],
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('取消')),
          FilledButton(
            onPressed: () async {
              if (nameCtrl.text.isEmpty || phoneCtrl.text.isEmpty) return;
              try {
                await ApiService.createReservation(
                  name: nameCtrl.text,
                  phone: phoneCtrl.text,
                  date: _formatDate(_selectedDate),
                  time: timeCtrl.text,
                  guestCount: int.tryParse(guestCtrl.text) ?? 2,
                  recommendedBy: recCtrl.text,
                  notes: noteCtrl.text,
                );
                if (mounted) {
                  Navigator.pop(ctx);
                  _loadData();
                }
              } catch (e) {
                if (mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('新增失敗: $e')),
                  );
                }
              }
            },
            child: const Text('建立預約'),
          )
        ],
      ),
    );
  }

  Color _getStatusColor(String status) {
    switch (status) {
      case 'PENDING':
        return Colors.orange;
      case 'CONFIRMED':
        return Colors.blue;
      case 'COMPLETED':
        return Colors.green;
      case 'CANCELLED':
        return Colors.grey;
      case 'NO_SHOW':
        return Colors.red;
      default:
        return Colors.blueGrey;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('煙深雨靜 · 店家預約看板'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadData,
            tooltip: '重新整理',
          ),
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () {
              Navigator.of(context).pushReplacement(
                MaterialPageRoute(builder: (_) => const LoginPage()),
              );
            },
            tooltip: '登出',
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _showAddReservationDialog,
        icon: const Icon(Icons.add),
        label: const Text('新增預約'),
        backgroundColor: const Color(0xff506958),
        foregroundColor: Colors.white,
      ),
      body: RefreshIndicator(
        onRefresh: _loadData,
        child: ListView(
          padding: const EdgeInsets.all(16.0),
          children: [
            // Date Header & Picker
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Row(
                  children: [
                    const Icon(Icons.calendar_today_rounded, color: Color(0xff506958)),
                    const SizedBox(width: 12),
                    Text(
                      _formatDate(_selectedDate),
                      style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                    ),
                    const Spacer(),
                    OutlinedButton.icon(
                      icon: const Icon(Icons.edit_calendar),
                      label: const Text('選擇日期'),
                      onPressed: () async {
                        final picked = await showDatePicker(
                          context: context,
                          initialDate: _selectedDate,
                          firstDate: DateTime(2025),
                          lastDate: DateTime(2030),
                        );
                        if (picked != null) {
                          setState(() => _selectedDate = picked);
                          _loadData();
                        }
                      },
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 12),

            // Statistics Header
            if (_todayStats != null) ...[
              Row(
                children: [
                  _buildStatCard('今日總預約', '${_todayStats!.totalReservations}', Colors.blue),
                  _buildStatCard('完成預約', '${_todayStats!.completedCount}', Colors.green),
                  _buildStatCard('未到店 (No Show)', '${_todayStats!.noShowCount}', Colors.red),
                ],
              ),
              const SizedBox(height: 16),
            ],

            // Search Bar & Filter Chips
            Row(
              children: [
                Expanded(
                  child: TextField(
                    decoration: const InputDecoration(
                      hintText: '搜尋姓名或電話...',
                      prefixIcon: Icon(Icons.search),
                      border: OutlineInputBorder(),
                      contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                    ),
                    onChanged: (val) => setState(() => _searchQuery = val),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),

            SingleChildScrollView(
              scrollDirection: Axis.horizontal,
              child: Row(
                children: [
                  _buildFilterChip('全部', 'ALL'),
                  _buildFilterChip('待確認', 'PENDING'),
                  _buildFilterChip('已確認', 'CONFIRMED'),
                  _buildFilterChip('已完成', 'COMPLETED'),
                  _buildFilterChip('已取消', 'CANCELLED'),
                  _buildFilterChip('未到店', 'NO_SHOW'),
                ],
              ),
            ),
            const SizedBox(height: 16),

            // Reservations List
            if (_isLoading)
              const Center(child: Padding(padding: EdgeInsets.all(32), child: CircularProgressIndicator()))
            else if (_filteredReservations.isEmpty)
              const Center(
                child: Padding(
                  padding: EdgeInsets.all(48.0),
                  child: Text('目前沒有符合條件的訂位紀錄', style: TextStyle(color: Colors.grey, fontSize: 16)),
                ),
              )
            else
              ..._filteredReservations.map((item) => _buildReservationCard(item)),
          ],
        ),
      ),
    );
  }

  Widget _buildFilterChip(String label, String code) {
    final isSelected = _selectedFilter == code;
    return Padding(
      padding: const EdgeInsets.only(right: 8.0),
      child: ChoiceChip(
        label: Text(label),
        selected: isSelected,
        onSelected: (val) {
          if (val) setState(() => _selectedFilter = code);
        },
      ),
    );
  }

  Widget _buildStatCard(String title, String value, Color color) {
    return Expanded(
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(12.0),
          child: Column(
            children: [
              Text(value, style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold, color: color)),
              const SizedBox(height: 4),
              Text(title, style: const TextStyle(fontSize: 12, color: Colors.grey)),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildReservationCard(ReservationItem item) {
    final statusColor = _getStatusColor(item.status);
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      elevation: 2,
      child: ListTile(
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        leading: CircleAvatar(
          backgroundColor: statusColor.withOpacity(0.15),
          child: Text(
            item.formattedTime,
            style: TextStyle(color: statusColor, fontWeight: FontWeight.bold, fontSize: 12),
          ),
        ),
        title: Row(
          children: [
            Text(item.customerName, style: const TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(width: 8),
            Chip(
              visualDensity: VisualDensity.compact,
              label: Text('${item.guestCount} 位', style: const TextStyle(fontSize: 12)),
            ),
          ],
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('電話：${item.customerPhone}'),
            if (item.recommendedBy != null && item.recommendedBy!.isNotEmpty)
              Text('推薦店員：${item.recommendedBy}', style: TextStyle(color: Colors.grey[700])),
            if (item.notes != null && item.notes!.isNotEmpty)
              Text('備註：${item.notes}', style: const TextStyle(color: Colors.orange, fontStyle: FontStyle.italic)),
          ],
        ),
        trailing: PopupMenuButton<String>(
          onSelected: (val) => _updateStatus(item, val),
          itemBuilder: (ctx) => [
            const PopupMenuItem(value: 'PENDING', child: Text('標示為：待確認')),
            const PopupMenuItem(value: 'CONFIRMED', child: Text('標示為：已確認')),
            const PopupMenuItem(value: 'COMPLETED', child: Text('標示為：已完成')),
            const PopupMenuItem(value: 'CANCELLED', child: Text('標示為：已取消')),
            const PopupMenuItem(value: 'NO_SHOW', child: Text('標示為：未到店')),
          ],
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
            decoration: BoxDecoration(
              color: statusColor.withOpacity(0.1),
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: statusColor),
            ),
            child: Text(
              item.statusDisplay,
              style: TextStyle(color: statusColor, fontWeight: FontWeight.bold),
            ),
          ),
        ),
      ),
    );
  }
}
