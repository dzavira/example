//
//  TableViewCellPromo.swift
//  ltsarc
//
//  Created by TRIAL on 18/10/21.
//

import UIKit

class TableViewCellPromo: UITableViewCell {

    @IBOutlet weak var imgdeskripsi: UIImageView!
    
    @IBOutlet weak var desktipsi: UITextView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    func configure(prom: forjason){
       // imgdeskripsi.image = prom.gambar
        desktipsi.text = prom.desc
     //   desktipsi.backgroundColor = .white
    }
//    override func setSelected(_ selected: Bool, animated: Bool) {
//        super.setSelected(selected, animated: animated)
//
//        // Configure the view for the selected state
//    }

}
