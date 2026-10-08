class ReservationItem {
  final int id;
  final String? publicToken;
  final int? customerId;
  final String customerName;
  final String customerPhone;
  final int? employeeId;
  final String? employeeName;
  final String reservationDate;
  final String reservationTime;
  final int guestCount;
  String status;
  final String? recommendedBy;
  final String? notes;
  final String? source;

  ReservationItem({
    required this.id,
    this.publicToken,
    this.customerId,
    required this.customerName,
    required this.customerPhone,
    this.employeeId,
    this.employeeName,
    required this.reservationDate,
    required this.reservationTime,
    required this.guestCount,
    required this.status,
    this.recommendedBy,
    this.notes,
    this.source,
  });

  factory ReservationItem.fromJson(Map<String, dynamic> json) {
    return ReservationItem(
      id: json['id'] is int ? json['id'] : int.parse(json['id'].toString()),
      publicToken: json['publicToken'],
      customerId: json['customerId'],
      customerName: json['customerName'] ?? '未提供姓名',
      customerPhone: json['customerPhone'] ?? '未提供電話',
      employeeId: json['employeeId'],
      employeeName: json['employeeName'],
      reservationDate: json['reservationDate'] ?? '',
      reservationTime: json['reservationTime'] ?? '',
      guestCount: json['guestCount'] ?? 1,
      status: json['status'] ?? 'PENDING',
      recommendedBy: json['recommendedBy'],
      notes: json['notes'],
      source: json['source'],
    );
  }

  String get formattedTime {
    if (reservationTime.length >= 5) {
      return reservationTime.substring(0, 5);
    }
    return reservationTime;
  }

  String get statusDisplay {
    switch (status) {
      case 'PENDING':
        return '待確認';
      case 'CONFIRMED':
        return '已確認';
      case 'COMPLETED':
        return '已完成';
      case 'CANCELLED':
        return '已取消';
      case 'NO_SHOW':
        return '未到店';
      default:
        return status;
    }
  }
}

class StatisticsItem {
  final int totalReservations;
  final int completedCount;
  final int noShowCount;
  final int cancelledCount;
  final double noShowRate;
  final double cancelRate;
  final int newCustomerCount;
  final int returningCustomerCount;
  final double returningRate;

  StatisticsItem({
    required this.totalReservations,
    required this.completedCount,
    required this.noShowCount,
    required this.cancelledCount,
    required this.noShowRate,
    required this.cancelRate,
    required this.newCustomerCount,
    required this.returningCustomerCount,
    required this.returningRate,
  });

  factory StatisticsItem.fromJson(Map<String, dynamic> json) {
    return StatisticsItem(
      totalReservations: json['totalReservations'] ?? 0,
      completedCount: json['completedCount'] ?? 0,
      noShowCount: json['noShowCount'] ?? 0,
      cancelledCount: json['cancelledCount'] ?? 0,
      noShowRate: (json['noShowRate'] ?? 0.0).toDouble(),
      cancelRate: (json['cancelRate'] ?? 0.0).toDouble(),
      newCustomerCount: json['newCustomerCount'] ?? 0,
      returningCustomerCount: json['returningCustomerCount'] ?? 0,
      returningRate: (json['returningRate'] ?? 0.0).toDouble(),
    );
  }
}
