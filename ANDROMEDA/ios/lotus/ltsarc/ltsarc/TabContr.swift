//
//  TabContr.swift
//  ltsarc
//
//  Created by TRIAL on 27/08/21.
//

import UIKit

class TabContr: UITabBarController {
    override func viewDidLoad() {
        super.viewDidLoad()
       // view.backgroundColor = .red
   //     addBack()
////
//        let nav = generateNavController(vc: PriceViewController(), title: "Price",image: #imageLiteral(resourceName: "iclts"))
//        let nav2 = generateNavController(vc: PromoViewController(), title: "Promo", image: #imageLiteral(resourceName: "icpromo"))
//        let navNul = generateNavController(vc: MainController(), title: "qr", image: #imageLiteral(resourceName: "iclts"))
//        let nav3 = generateNavController(vc: ContactUsViewController(), title: "Contact Us", image:#imageLiteral(resourceName: "iccall"))
//        let nav4 = generateNavController(vc: SettingViewController(), title: "Setting", image: #imageLiteral(resourceName: "icgear"))
//
//        UINavigationBar.appearance().prefersLargeTitles = true
//        viewControllers = [nav, nav2, navNul, nav3, nav4]
        
        
        
        
        
      //  view.backgroundColor = .yellow
//        let tabBar = UITabBarController()
//        let nav = generateNavController(vc: MainController(), title: "Price",image: #imageLiteral(resourceName: "iclts"))
//        let nav2 = generateNavController(vc: MainController(), title: "Promo", image: #imageLiteral(resourceName: "icpromo"))
//        let navNul = generateNavController(vc: MainController(), title: "", image: #imageLiteral(resourceName: "iclts"))
//        let nav3 = generateNavController(vc: MainController(), title: "Contact Us", image:#imageLiteral(resourceName: "iccall"))
//        let nav4 = generateNavController(vc: MainController(), title: "Setting", image: #imageLiteral(resourceName: "icgear"))
//        let nav1 = PriceViewController()
//        let nav2 = PromoViewController()
//        let nav4 = ContactUsViewController()
//        let nav5 = SettingViewController()
//        nav1.title = "Price"
//        nav2.title = "Promo"
//        nav4.title = "Contact Us"
//        nav5.title = "Setting"
        
//        tabBar.setViewControllers([nav,nav2,nav3,nav4], animated: false)
//        tabBar.modalPresentationStyle = .fullScreen
//        present(tabBar, animated: false)
       
    }
    func addBack(){
        let button = UIButton(frame: CGRect(x: 12,  y: 20,  width: 25,  height: 25))
              // button.setTitle("Back",for: .normal)
               button.setImage(UIImage(named: "bakcwhite.png"), for: .normal)
               button.setTitleColor(.systemBlue, for: .normal)
       
        button.addTarget(self, action: #selector(btBack(_:)), for: .touchUpInside)
               self.view.addSubview(button)
        
    }
    @objc func btBack(_ sender:UIButton!)
        {
      //  let nabBar = NaviBar()
       // self.navigationController?.pushViewController(nabBar, animated: false)
        performSegue(withIdentifier: "segHalUtama", sender: self)
        }
//    fileprivate func generateNavController(vc: UIViewController, title: String, image: UIImage) -> UINavigationController{
//        //let navController = UINavigationController(rootViewController: vc)
//        let navController = UINavigationController(rootViewController: vc)
//        navController.modalPresentationStyle = .fullScreen
//
//        navController.title = title
//        if(!title.isEmpty){
//            navController.tabBarItem.image = image
//        }
//        //   navController.present(navController, animated: false)
//
//        return navController
//    }
    
//
//class PriceViewController: UIViewController{
//     override func viewDidLoad() {
//        super.viewDidLoad()
//        view.backgroundColor = .blue
//      //  title = "Price"
//    }
//}
//    
//    
//    
//    class PromoViewController: UIViewController{
//         override func viewDidLoad() {
//            super.viewDidLoad()
//            view.backgroundColor = .green
//          //  title = "Promo"
//        }
//    }
//
//    class ContactUsViewController: UIViewController{
//         override func viewDidLoad() {
//            super.viewDidLoad()
//            view.backgroundColor = .gray
//            title = "Contact Us"
//        }
//    }
//
//    class SettingViewController: UIViewController{
//         override func viewDidLoad() {
//            super.viewDidLoad()
//            view.backgroundColor = .magenta
//            title = "Setting"
//        }
//    }
 
}
