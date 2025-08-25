import { createBrowserRouter } from "react-router"
import BasicLayout from "../layouts/basicLayout"
import { lazy, Suspense } from "react"

const Loading = () => <div>Loading...</div>

const Main = lazy(() => import ("../pages/mainPage"))
const Register = lazy(() => import("../pages/registerPage"))
const Login = lazy(() => import("../pages/loginPage"))
const Write = lazy(() => import("../pages/writePage"))
const Read = lazy(() => import("../pages/readPage"))
const Comment = lazy(() => import("../pages/commentPage"))
const Edit = lazy(() => import("../pages/editPage"))
const Search = lazy(() => import("../pages/searchPage"))
const Category = lazy(() => import("../pages/categoryPage"))
const MyPage = lazy(() => import("../pages/myPage"))
const EditProfile = lazy(() => import("../pages/editProfilePage"))
const Chat = lazy(() => import("../pages/chatPage"))
const Notify = lazy(() => import("../pages/notificationPage"))
const Video = lazy(() => import("../pages/videoCallPage"))

const router = createBrowserRouter([
    {
        path: "/",
        Component: BasicLayout,
        children: [
            {
                index: true,
                element: <Suspense fallback={<Loading />}><Main /></Suspense>
            },
            {
                path: "register",
                element: <Suspense fallback={<Loading />}><Register /></Suspense>
            },
            {
                path: "login",
                element: <Suspense fallback={<Loading />}><Login /></Suspense>
            },
            {
                path: "write",
                element: <Suspense fallback={<Loading />}><Write /></Suspense>
            },
            {
                path: "read/:articleId",
                element: <Suspense fallback={<Loading />}><Read /></Suspense>
            },
            {
                path: "comment",
                element: <Suspense fallback={<Loading />}><Comment /></Suspense>
            },
            {
                path: "edit/:articleId",
                element: <Suspense fallback={<Loading />}><Edit /></Suspense>
            },
            {
                path: "search",
                element: <Suspense fallback={<Loading />}><Search /></Suspense>
            },
            {
                path: "category/:category",
                element: <Suspense fallback={<Loading />}><Category /></Suspense>
            },
            {
                path: "mypage",
                element: <Suspense fallback={<Loading />}><MyPage /></Suspense>
            },
            {
                path: "profile/edit",
                element: <Suspense fallback={<Loading />}><EditProfile /></Suspense>
            },
            {
                path: "chat/:userId",
                element: <Suspense fallback={<Loading />}><Chat /></Suspense>
            },
            {
                path: "notifications",
                element: <Suspense fallback={<Loading />}><Notify /></Suspense>
            },
            {
                path: "video-call/:roomId",
                element: <Suspense fallback={<Loading />}><Video /></Suspense>
            },
        ]
    }
])

export default router;