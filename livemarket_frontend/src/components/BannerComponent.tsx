import React from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import { Navigation, Pagination, Autoplay } from "swiper/modules";
import banner1 from '../assets/banner1.png'
import banner2 from '../assets/banner2.png'

// Swiper 스타일 import
import "swiper/css";
import "swiper/css/pagination";
import "swiper/css/navigation";

const BannerComponent: React.FC = () => {
  const banners = [
    banner1,
    banner2
  ];

  return (
    <div className="w-full max-w-4xl mx-auto mt-4">
      <Swiper
        modules={[Autoplay, Pagination, Navigation]}
        spaceBetween={4} // 기존 10 → 4로 줄임
        slidesPerView={1}
        loop={true}
        autoplay={{ delay: 3000, disableOnInteraction: false }}
        pagination={{ clickable: true }}
        navigation
        >
        {banners.map((src, index) => (
            <SwiperSlide key={index}>
            <img
                src={src}
                alt={`banner-${index}`}
                className="w-full h-48 object-contain rounded-lg"
            />
            </SwiperSlide>
        ))}
      </Swiper>
    </div>
  );
};

export default BannerComponent;
