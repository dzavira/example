//
//  PromoCn.swift
//  ltsarc
//
//  Created by TRIAL on 06/10/21.
//

import UIKit
import Alamofire
import SwiftyJSON
import AlamofireImage


class PromoCn: UIViewController {
     
     
    var prlist = [forjason]()
    
    @IBOutlet weak var bttokopedia: UIImageView!
    @IBOutlet weak var btinstagram: UIImageView!
    @IBOutlet weak var tblPromo: UITableView!
    @IBOutlet weak var hdrPromo: UIView!
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .red
        self.tabBarController?.tabBar.isHidden = true
        tblPromo.dataSource = self
        tblPromo.backgroundColor = .white
        getJsonDes()
        
        let tapIG = UITapGestureRecognizer(target: self, action: #selector(self.imgIG))
        btinstagram.addGestureRecognizer(tapIG)
        btinstagram.isUserInteractionEnabled = true
        
        let tapTK = UITapGestureRecognizer(target: self, action: #selector(self.imgTK))
        bttokopedia.addGestureRecognizer(tapTK)
        bttokopedia.isUserInteractionEnabled = true
    
    }
    @objc func imgIG(sender: UITapGestureRecognizer)
    {
         if sender.state == .ended {
            guard let instagram = URL(string: "https://www.instagram.com/_u/lotusarchigold") else { return }
               UIApplication.shared.open(instagram)
                            print("instagram")
        }
    }
    @objc func imgTK(sender: UITapGestureRecognizer)
    {
         if sender.state == .ended {
            guard let instagram = URL(string: "https://tokopedia.com/lotusarchi") else { return }
               UIApplication.shared.open(instagram)
                            print("tokopedia")
        }
    }
    func getJsonDes(){
        let parameters = [
                "page": "1"
            ]
        let urlPromo = "https://hsl.puragroup.com/lotusarchi/api/promo"
        AF.request(urlPromo, method: .post, parameters: parameters).responseJSON{
            (responds) in
            switch responds.result
            {
            case .success(let value):
                let dtjson = JSON(value)
                for i in 0 ..< dtjson["data"].count{
                 
                    self.prlist.append(forjason(gambar: dtjson["data"][i]["gambar"].stringValue, desc: dtjson["data"][i]["desc"].stringValue))
                }
                self.tblPromo.reloadData()
                
                print(self.prlist)
            case .failure(let error):
                print("error di \(error.localizedDescription)")
            }
        }
        
        
        
    }
    
    
}
extension PromoCn: UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return prlist.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as! TableViewCellPromo
        
      //  let deksripsi = prlist[indexPath.row].desc
        cell.desktipsi.text = prlist[indexPath.row].desc as? String
        let urlImg = prlist[indexPath.row].gambar as? String
        
        AF.request(urlImg!).responseImage {
            (responds) in
            switch responds.result
            {
            case .success(let image):
                DispatchQueue.main.async {
                    cell.imgdeskripsi.image = image
                }
            case .failure(let error):
                print(error.localizedDescription)
            }
             
        }
        
        
        return cell
    }
    
    
}
 
struct forjason {
    let gambar, desc : String
}
