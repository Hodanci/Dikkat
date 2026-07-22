# Dikkat - Motosiklet Hız Kontrolü Uygulaması

🏍️ Motosiklet sürücüleri için gerçek zamanlı hız sınırı kontrolü ve sesli uyarı sistemi.

## Özellikler

✅ **Gerçek Zamanlı Hız Algılama** - GPS ile anlık hız takibi
✅ **Sesli Uyarı Sistemi** - Hız sınırı aşıldığında 3 beep sesi
✅ **4 Yol Tipi Desteği**:
   - Şehir İçi: 50 km/h (Yeşil)
   - Çift Yönlü Yol: 80 km/h (Sarı)
   - Bölünmüş Yol: 90 km/h (Turuncu)
   - Paralı Otoyol: 100 km/h (Kırmızı)

✅ **Görsel Göstergeler** - Durum rengine göre değişen ikonlar
✅ **Hız Aşımı Yüzdesi** - Ne kadar fazla hız yaptığını gösterir
✅ **Basit Kullanıcı Arayüzü** - Kolay ve anlaşılır tasarım

## Kurulum

### Gereksinimler
- Android 7.0+ (API 24)
- Location Permission
- Google Play Services

### Build
```bash
./gradlew build
./gradlew installDebug
```

## Kullanım

1. Uygulamayı aç
2. Yol tipini seç (Spinner'dan)
3. "Başla" butonuna tıkla
4. Hız sınırı aşıldığında sesli uyarı alacaksın
5. "Durdur" butonuna tıkla izlemeyi sonlandırmak için

## Mimari

- **SpeedLimitManager** - Hız sınırları ve kategori yönetimi
- **SoundAlertManager** - Sesli uyarı sistemi
- **SpeedLocationManager** - GPS konum ve hız takibi
- **MainActivity** - Ana kullanıcı arayüzü

## İzinler

- `ACCESS_FINE_LOCATION` - Kesin konum bilgisi
- `ACCESS_COARSE_LOCATION` - Yaklaşık konum bilgisi
- `MODIFY_AUDIO_SETTINGS` - Ses ayarlarını değiştirme
- `RECORD_AUDIO` - Ses kaydı

## Lisans

MIT License - Açık olarak kullanılabilir.

## Geliştirici

👤 **Hodanci**
