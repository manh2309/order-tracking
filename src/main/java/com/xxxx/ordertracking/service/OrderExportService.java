package com.xxxx.ordertracking.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.xxxx.ordertracking.dto.response.OrderSummary;
import com.xxxx.ordertracking.enums.OrderStatus;
import com.xxxx.ordertracking.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderExportService {
    private static final int BATCH_SIZE = 500;
    private static final int ROWS_PER_SHEET = 1_000_000;
    private static final List<List<String>> HEADER = List.of("Mã đơn", "Ngày tạo (UTC)", "Trạng thái",
            "Tiền hàng", "Giảm giá", "Phí vận chuyển", "Tổng tiền", "Tiền tệ")
            .stream().map(List::of).toList();
    private final OrderRepository orders;

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public void write(String accountId, OrderStatus status, OutputStream output) {
        String afterId = "";
        int written = 0;
        int sheetNumber = 1;
        try (ExcelWriter writer = EasyExcel.write(output).autoCloseStream(false).head(HEADER).build()) {
            WriteSheet sheet = sheet(sheetNumber);
            writer.write(List.of(), sheet);
            while (true) {
                var batch = orders.exportBatch(accountId, status, afterId, PageRequest.of(0, BATCH_SIZE));
                if (batch.isEmpty()) break;
                if (written == ROWS_PER_SHEET) {
                    sheet = sheet(++sheetNumber);
                    written = 0;
                }
                writer.write(batch.stream().map(OrderExportService::cells).toList(), sheet);
                afterId = batch.getLast().id();
                written += batch.size();
                if (batch.size() < BATCH_SIZE) break;
            }
        }
    }

    private static WriteSheet sheet(int number) {
        return EasyExcel.writerSheet(number - 1, "Đơn hàng " + number).build();
    }

    private static List<?> cells(OrderSummary order) {
        return List.of(order.code(), order.createdAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                order.status().name(), order.subtotal(), order.discountAmount(), order.shippingFee(),
                order.grandTotal(), order.currency());
    }
}
