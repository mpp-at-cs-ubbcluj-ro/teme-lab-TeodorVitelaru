package turism.model;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Converter(autoApply = true)
public class LocalDateTimeStringConverter implements AttributeConverter<LocalDateTime, String> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");  // Asigură-te că acest format se potrivește cu cel din baza ta de date.

    @Override
    public String convertToDatabaseColumn(LocalDateTime localDateTime) {
        return (localDateTime == null ? null : formatter.format(localDateTime));
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dbData) {
        return (dbData == null ? null : LocalDateTime.parse(dbData, formatter));
    }
}
