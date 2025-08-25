import { useParams } from "react-router";
import CommentComponent from "../components/CommentComponent";

function CommentPage() {
    const { articleId } = useParams();
    return (  
        <CommentComponent articleId={articleId} />
    );
}

export default CommentPage;