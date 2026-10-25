# Buku Utang WiFi — PWA

## Isi
- `index.html`, `style.css`, `app.js`: aplikasi.
- `manifest.webmanifest`, `sw.js`, `icon.svg`: komponen PWA.
- Penyimpanan IndexedDB di browser perangkat.

## Deployment Netlify dari Android
1. Unduh ZIP deployment, ekstrak di pengelola file jika perlu.
2. Buka Netlify melalui browser, masuk/daftar.
3. Buka halaman deploy manual Netlify Drop (https://app.netlify.com/drop).
4. Unggah folder hasil ekstraksi (atau ZIP sesuai UI Netlify yang tersedia).
5. Buka URL HTTPS hasil deploy. Cek aplikasi dapat dimuat dan tambah data percobaan.
6. Untuk update: unggah ulang paket build. Ganti versi cache di `sw.js` ketika mengubah aset.

## Instalasi Android
Buka URL HTTPS menggunakan Chrome. Menu ⋮ > Tambahkan ke layar utama / Install app. Nama menu dapat berbeda menurut browser. Aplikasi tetap memakai penyimpanan profil browser tersebut.

## Backup & restore
Menu ☰ > Ekspor backup JSON simpan ke Drive/penyimpanan eksternal secara rutin. CSV mengekspor riwayat transaksi. Restore melakukan merge; ID sama dilewati dan tidak mengganti record yang sudah ada. Simpan salinan backup sebelum restore.

## Catatan keuangan
- Nominal utang awal: input satuan ribuan (100 = Rp100.000).
- Transaksi tambahan: rupiah penuh.
- Pembayaran berlebih ditolak.
- Koreksi menyimpan arsip versi dan audit; pembatalan tetap tersimpan.
- Data agregat dashboard mencakup transaksi yang sah, setelah koreksi.

## Batasan & keamanan
Ini aplikasi lokal satu perangkat, bukan sinkronisasi cloud. Data dapat hilang bila data situs/browser dihapus, profil perangkat diganti, atau storage dibersihkan. Backup terpisah diperlukan. Tidak ada login, enkripsi aplikasi, atau sinkronisasi lintas perangkat. Gunakan kunci layar perangkat dan jangan bagikan file backup karena berisi data finansial pelanggan. Browser dapat membatasi penyimpanan.

## Checklist pengujian manual
- [ ] HTTPS membuka halaman dan ikon install tersedia bila browser mendukung.
- [ ] Tombol + membuka form terlihat; tutup dengan X, batal, dan backdrop.
- [ ] Form empat field tampil dan validasi wajib bekerja.
- [ ] Nilai 100 membuat transaksi utang Rp100.000; nilai 0 hanya menambah profil.
- [ ] Tutup dan buka ulang: data bertahan.
- [ ] Pelanggan terurut sisa piutang terbesar ke terkecil.
- [ ] Tambah utang, pembayaran tepat, pembayaran berlebih ditolak.
- [ ] Koreksi wajib alasan dan histori audit tampak.
- [ ] Pembatalan mencatat alasan; saldo tidak negatif.
- [ ] Backup JSON, ekspor CSV, restore file valid, file rusak ditolak.
- [ ] Uji mode offline setelah shell pernah termuat online.
- [ ] Periksa Console browser bila tersedia.

## Catatan validasi
Paket ini disusun sebagai implementasi awal. Checklist di atas adalah prosedur pengujian, bukan klaim bahwa semua skenario sudah dieksekusi pada perangkat Android/Netlify. Review logika audit/restore dan uji perangkat nyata sebelum dijadikan sumber pembukuan utama.


## Dashboard profesional — metrik piutang
- **Total Tagihan Belum Lunas** menunjukkan nilai saldo utang berjalan (utang valid dikurangi pembayaran valid), sehingga turun setiap pembayaran masuk.
- **Total Pendapatan** tetap merupakan akumulasi pembayaran valid.
- **Pelanggan Belum Lunas** menghitung pelanggan aktif dengan saldo positif, bukan nominal rupiah.
- **Pelanggan** tetap menunjukkan jumlah akun yang tersimpan.
- Riwayat transaksi tetap dipertahankan; pembayaran tidak menghapus catatan tagihan lama.
