import 'package:flutter_test/flutter_test.dart';
import 'package:sma_app/main.dart';

void main() {
  testWidgets('App launches', (WidgetTester tester) async {
    await tester.pumpWidget(const SmaApp());
    await tester.pump();
  });
}
