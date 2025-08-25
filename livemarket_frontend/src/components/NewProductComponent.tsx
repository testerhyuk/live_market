import React, { useEffect, useState } from "react";
import { getAll, getArticleImages, type Article } from "../api/articleApi";
import { Swiper, SwiperSlide } from "swiper/react";
import { Navigation, Pagination, Autoplay } from "swiper/modules";
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";
import { useNavigate } from "react-router";

const NewProductComponent: React.FC = () => {
  const [articles, setArticles] = useState<Article[]>([]);
  const [imagesMap, setImagesMap] = useState<Record<string, string[]>>({});
  const navigate = useNavigate()

  useEffect(() => {
    const fetchArticles = async () => {
      try {
        const data = await getAll();
        setArticles(data);

        const imagesPromises = data.map(async (article) => {
          const imgs = await getArticleImages(article.articleId);
          return [article.articleId, imgs] as [string, string[]];
        });

        const imagesEntries = await Promise.all(imagesPromises);
        const imagesObj = Object.fromEntries(imagesEntries);
        setImagesMap(imagesObj);
      } catch (err) {
        console.error(err);
      }
    };

    fetchArticles();
  }, []);

  return (
    <div className="max-w-3xl mx-auto py-4">
      <h2 className="text-lg font-semibold mb-4">새로 등록 된 상품</h2>
      <Swiper
        modules={[Navigation, Pagination, Autoplay]}
        navigation
        pagination={{ clickable: true }}
        autoplay={{ delay: 3000 }}
        spaceBetween={16}
        slidesPerView={1.5}
        grabCursor
        style={{ paddingBottom: "30px" }}
      >
        {articles.map((article) => (
          <SwiperSlide key={article.articleId}>
            <div className="bg-white rounded-lg shadow p-2 cursor-pointer" onClick={() => navigate(`/read/${article.articleId}`)}>
                <div className="w-full h-36 bg-gray-100 rounded overflow-hidden mb-3">
                {imagesMap[article.articleId] && imagesMap[article.articleId][0] ? (
                    <img
                    src={imagesMap[article.articleId][0]}
                    alt={article.title}
                    className="w-full h-full object-cover"
                    />
                ) : (
                    <div className="w-full h-full bg-gray-200" />
                )}
                </div>
                <div className="px-1">
                <h3 className="text-sm font-semibold line-clamp-1 mb-1">{article.title}</h3>
                <p className="text-sm font-bold text-red-500">
                    {`${article.price.toLocaleString()}원`}
                </p>
                </div>
            </div>
          </SwiperSlide>
        ))}
      </Swiper>
    </div>
  );
};

export default NewProductComponent;
