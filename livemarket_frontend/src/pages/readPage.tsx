import { useParams } from "react-router";
import ReadComponent from "../components/ReadComponent";

function ReadPage() {
    const { articleId } = useParams<{ articleId: string }>();

    return (
        <div>
            {articleId ? <ReadComponent articleId={articleId} /> : <p>잘못된 요청입니다.</p>}
        </div>
    );
}

export default ReadPage;