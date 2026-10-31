package DAO;

import java.util.List;

/**
 * Giao diện truy cập dữ liệu chung cho mọi entity. Màn hình chỉ làm việc với giao diện này,
 * nên khi chuyển sang SQL Server chỉ cần viết lại thân các lớp DAO, không phải sửa giao diện.
 */
public interface IDAO<T> {
    /** Lấy toàn bộ bản ghi. */
    List<T> getAll();

    /** Thêm mới. @return true nếu thành công. */
    boolean them(T t);

    /** Cập nhật theo mã. @return true nếu thành công. */
    boolean capNhat(T t);

    /** Xóa theo mã. @return true nếu thành công. */
    boolean xoa(T t);

    /** Sinh mã kế tiếp (ví dụ NV009). */
    String taoMaMoi();
}
