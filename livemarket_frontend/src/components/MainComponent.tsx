import BannerComponent from "./BannerComponent";
import HotProductComponent from "./HotProductComponent";
import NewProductComponent from "./NewProductComponent";

function MainComponent() {
    return (  
        <div>
            <div>
                <BannerComponent />
            </div>
            <div>
                <NewProductComponent />
            </div>
            <div>
                <HotProductComponent />
            </div>
        </div>
    );
}

export default MainComponent;